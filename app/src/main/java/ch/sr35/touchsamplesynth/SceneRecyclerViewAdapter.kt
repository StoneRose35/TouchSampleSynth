package ch.sr35.touchsamplesynth

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import ch.sr35.touchsamplesynth.databinding.SceneItemBinding
import ch.sr35.touchsamplesynth.model.SceneP


class SceneRecyclerViewAdapter(
    private val values: List<SceneP>
) : RecyclerView.Adapter<SceneRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            SceneItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: SceneItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.sceneName

        override fun toString(): String {
            return super.toString() + " '" + idView.text + "'"
        }
    }

}