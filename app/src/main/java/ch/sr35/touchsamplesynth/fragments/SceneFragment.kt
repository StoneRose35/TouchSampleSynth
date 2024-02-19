package ch.sr35.touchsamplesynth.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
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
import ch.sr35.touchsamplesynth.SceneRecyclerViewAdapter
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.model.SceneP
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import java.io.File


/**
 * A fragment representing a list of Items.
 */
class SceneFragment(private var scenes: ArrayList<SceneP>) : Fragment() {
    private var scenesList: RecyclerView?=null



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
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                ItemTouchHelper.RIGHT -> { // delete
                    val alertDlgBuilder = AlertDialog.Builder(context as TouchSampleSynthMain)
                        .setMessage((context as TouchSampleSynthMain).getString(R.string.alert_dialog_really_delete))
                        .setPositiveButton((context as TouchSampleSynthMain).getString(R.string.yes)) { _, _ ->
                            scenes.removeAt(viewHolder.absoluteAdapterPosition)
                            scenesList?.adapter?.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
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
            }
            builder.setNegativeButton(
                (context as TouchSampleSynthMain).getString(android.R.string.cancel)
            ) {
                    dialog, _ -> dialog.cancel()
            }

            builder.show()
        }

        buttonExport.setOnClickListener {
            /*
            val dialogProperties = DialogProperties()
            dialogProperties.selection_mode = DialogConfigs.SINGLE_MODE
            dialogProperties.selection_type = DialogConfigs.DIR_SELECT
            dialogProperties.root = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.offset = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.extensions=null
            dialogProperties.show_hidden_files=false
            val dirPickerDialog=FilePickerDialog(context,dialogProperties)
            dirPickerDialog.setTitle(R.string.selectFolder)
            dirPickerDialog.setDialogSelectionListener {
                val gson=Gson()
                val jsonOut = gson.toJson((context as TouchSampleSynthMain).allScenes)
                val f = File(it[0],"touchSampleSynthScenes1.json")
                if(f.exists())
                {
                    f.delete()
                }
                f.writeText(jsonOut)
                f.setReadable(true,false)
                f.setWritable(true,false)
                f.setExecutable(true,false)

            }
            dirPickerDialog.show()
            */


            val mainDir = ((context as TouchSampleSynthMain).filesDir.absolutePath)
            val gson=Gson()
            val jsonOut = gson.toJson((context as TouchSampleSynthMain).allScenes)
            Log.i(TAG, "exporting scenes as json")
            val f = File(mainDir + File.separator + "touchSampleSynthScenes1.json")
            if(f.exists())
            {
                f.delete()
            }
            f.writeText(jsonOut)
            val sb = Snackbar.make(it,resources.getText(R.string.exportSuccessful),1000)
            sb.show()
        }

        buttonImport.setOnClickListener {
            /*
            val dialogProperties = DialogProperties()
            dialogProperties.selection_mode = DialogConfigs.SINGLE_MODE
            dialogProperties.selection_type = DialogConfigs.FILE_SELECT
            dialogProperties.root = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.offset = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.extensions= arrayOf("json")
            dialogProperties.show_hidden_files=false
            val filePickerDialog=FilePickerDialog(context,dialogProperties)
            filePickerDialog.setTitle(R.string.selectImportFile)
            filePickerDialog.setDialogSelectionListener { it1 ->
                val gson=Gson()
                val f = File(it1[0])
                val jsondata=f.readText()
                try {
                    val jsonobj = gson.fromJson(jsondata, Array<SceneP>::class.java)
                    (context as TouchSampleSynthMain).allScenes.clear()
                    (context as TouchSampleSynthMain).allScenes.addAll(jsonobj)
                } catch (e: Exception)
                {
                    when(e) {is JsonSyntaxException, is JsonParseException -> {
                            this.view?.let {
                                val sb = Snackbar.make(it,resources.getText(R.string.importErrorMessage),1000)
                                sb.show()
                            }
                        }
                    }
                }
            }
            filePickerDialog.show()
             */
            val mainDir = ((context as TouchSampleSynthMain).filesDir.absolutePath)
            val gson=Gson()
            val f = File(mainDir + File.separator + "touchSampleSynthScenes1.json")
            if (f.exists())
            {
                val jsondata=f.readText()
                try {
                    val jsonobj = gson.fromJson(jsondata, Array<SceneP>::class.java)
                    (context as TouchSampleSynthMain).allScenes.clear()
                    (context as TouchSampleSynthMain).allScenes.addAll(jsonobj)
                } catch (e: Exception)
                {
                    when(e) {is JsonSyntaxException, is JsonParseException -> {
                        this.view?.let {
                            val sb = Snackbar.make(it,resources.getText(R.string.importErrorMessage),1000)
                            sb.show()
                        }
                    }
                    }
                }
            }
            else
            {
                val sb = Snackbar.make(it,resources.getText(R.string.importFileNotFound),1000)
                sb.show()
            }
        }

        return view
    }

}