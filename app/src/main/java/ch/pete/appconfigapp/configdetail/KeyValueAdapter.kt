package ch.pete.appconfigapp.configdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.model.KeyValue

class KeyValueAdapter(
    private val onItemClickListener: ((KeyValue) -> Unit)?
) :
    ListAdapter<KeyValue, KeyValueAdapter.KeyValueViewHolder>(
        DIFF_CALLBACK
    ) {

    class KeyValueViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val key: TextView = rootView.findViewById(R.id.key)
        val value: TextView = rootView.findViewById(R.id.value)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KeyValueViewHolder {
        val rootView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.key_value_list_item, parent, false)
        return KeyValueViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: KeyValueViewHolder, position: Int) {
        val keyValue = getItem(position)
        holder.key.text = keyValue.key
        holder.value.text = keyValue.value

        onItemClickListener?.let {
            holder.itemView.setOnClickListener {
                it(keyValue)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<KeyValue>() {
                override fun areItemsTheSame(oldItem: KeyValue, newItem: KeyValue) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: KeyValue, newItem: KeyValue) =
                    oldItem == newItem
            }
    }
}
