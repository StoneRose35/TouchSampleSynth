package ch.sr35.touchsamplesynth.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.SCENES_FILE_NAME
import ch.sr35.touchsamplesynth.SceneRecyclerViewAdapter
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.model.SceneListP
import ch.sr35.touchsamplesynth.model.SceneP
import ch.sr35.touchsamplesynth.model.importDoneFlag
import ch.sr35.touchsamplesynth.model.importMode
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment representing a list of Items.
 */
class SceneFragment() : Fragment() {
    private var scenesList: RecyclerView?=null
    private var scenes: ArrayList<SceneP> = ArrayList<SceneP>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scene_list, container, false)
        scenesList = view.findViewById(R.id.sceneList)
        val buttonAdd = view.findViewById<Button>(R.id.sceneAdd)
        scenesList?.layoutManager = LinearLayoutManager(context)
        scenes = (context as TouchSampleSynthMain).allScenes
        scenesList?.adapter = SceneRecyclerViewAdapter(scenes)
        val buttonImport = view.findViewById<Button>(R.id.sceneImport)
        val buttonExport = view.findViewById<Button>(R.id.sceneExport)



        val touchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT)){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val p1 = viewHolder.absoluteAdapterPosition
                val p2 = target.absoluteAdapterPosition

                    val swap = scenes[p2]
                    scenes[p2] = scenes[p1]
                    scenes[p1] = swap
                recyclerView.adapter?.notifyItemMoved(p1,p2)
                (context as TouchSampleSynthMain).scenesArrayAdapter?.notifyDataSetChanged()
                if (p1 == (context as TouchSampleSynthMain).getCurrentSceneIndex() || p2 == (context as TouchSampleSynthMain).getCurrentSceneIndex()) {
                    (context as TouchSampleSynthMain).reloadCurrentScene()
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                ItemTouchHelper.RIGHT -> { // delete
                    val alertDlgBuilder = AlertDialog.Builder(context as TouchSampleSynthMain)
                        .setMessage((context as TouchSampleSynthMain).getString(R.string.alert_dialog_really_delete))
                        .setPositiveButton((context as TouchSampleSynthMain).getString(R.string.yes)) { _, _ ->

                            val currentlyLoadedSceneDeleting = (scenes[viewHolder.absoluteAdapterPosition] == (context as TouchSampleSynthMain).getCurrentScene())
                            scenes.removeAt(viewHolder.absoluteAdapterPosition)
                            scenesList?.adapter?.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                            (context as TouchSampleSynthMain).scenesArrayAdapter?.notifyDataSetChanged()
                            if ( currentlyLoadedSceneDeleting && scenes.size > 0)
                            {
                                (context as TouchSampleSynthMain).reloadCurrentScene()
                            }
                        }
                        .setNegativeButton((context as TouchSampleSynthMain).getString(R.string.no)) { _, _ -> }
                    val alertDlg = alertDlgBuilder.create()
                    alertDlg.show()
                }
                ItemTouchHelper.LEFT -> { // copy item
                    val newScene = scenes[viewHolder.layoutPosition].clone() as SceneP
                    try {
                        val splitName = newScene.name.split(" ")
                        val sceneIdx = splitName.last().toInt() + 1
                        val newName = splitName.dropLast(1).joinToString(" ") + " " + sceneIdx.toString()
                        newScene.name=newName
                    } catch (_: Exception)
                    {
                        newScene.name = newScene.name + " 1"
                    }
                    scenes.add(viewHolder.layoutPosition, newScene)
                    scenesList?.adapter?.notifyItemInserted(viewHolder.layoutPosition)
                    scenesList?.adapter?.notifyItemChanged(viewHolder.layoutPosition+1)
                    (context as TouchSampleSynthMain).scenesArrayAdapter?.notifyDataSetChanged()
                }
            }
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(
                    ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
                    ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
            }
        }

        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(scenesList)

        buttonAdd.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Title")

            val input = EditText(context)

            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton(
                (context as TouchSampleSynthMain).getString(android.R.string.ok)
            ) { _,_ ->
                val scene = SceneP()
                scene.name = input.text.toString()
                scenes.add(scene)
                scenesList?.adapter?.notifyItemInserted(scenes.size-1)
                (context as TouchSampleSynthMain).scenesArrayAdapter?.notifyDataSetChanged()
            }
            builder.setNegativeButton(
                (context as TouchSampleSynthMain).getString(android.R.string.cancel)
            ) {
                    dialog, _ -> dialog.cancel()
            }

            builder.show()
        }

        buttonExport.setOnClickListener {

            val jsonobj = SceneListP.importFromJson(context as TouchSampleSynthMain)
            jsonobj?.scenes?.clear()
            jsonobj?.scenes?.addAll((context as TouchSampleSynthMain).getScenesList())
            val jsonStringScenes = SceneListP.scenesToJsonString(context as TouchSampleSynthMain)
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.setType("text/plain")
            sendIntent.putExtra(Intent.EXTRA_TEXT, jsonStringScenes)
            startActivity(Intent.createChooser(sendIntent,getString(R.string.shareScenes)))
            if (SceneListP.exportAsJson(SCENES_FILE_NAME,context as TouchSampleSynthMain)) {
                val sb = Snackbar.make(it, resources.getText(R.string.exportSuccessful), 1000)
                sb.show()
            }
        }

        buttonImport.setOnClickListener {
            val jsonobj = SceneListP.importFromJson(context as TouchSampleSynthMain)
            if (jsonobj == null)
            {
                this.view?.let {
                    val sb = Snackbar.make(it,resources.getText(R.string.importErrorMessage),1000)
                    sb.show()
                }
                return@setOnClickListener
            }
            val sb = Snackbar.make(it, resources.getText(R.string.importSuccessful), 1000)
            sb.show()
            jsonobj.importOntoDevice(context as TouchSampleSynthMain,importMode.REPLACE,importDoneFlag.DO_NOT_CHANGE)
            (context as TouchSampleSynthMain).scenesListDirty = true
            (context as TouchSampleSynthMain).loadSceneWithWaitIndicator(0)
            scenesList?.adapter?.notifyDataSetChanged()
        }

        return view
    }

}