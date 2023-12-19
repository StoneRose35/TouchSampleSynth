package ch.sr35.touchsamplesynth.fragments

import android.app.AlertDialog
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
import ch.sr35.touchsamplesynth.SceneRecyclerViewAdapter
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.model.SceneP


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

        return view
    }

}