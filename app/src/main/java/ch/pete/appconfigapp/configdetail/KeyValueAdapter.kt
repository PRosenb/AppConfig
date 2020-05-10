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
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.config_entry_list_item.view.delete
import kotlinx.android.synthetic.main.config_entry_list_item.view.mainLayout
import kotlinx.android.synthetic.main.config_entry_list_item.view.swipeLayout
import kotlinx.android.synthetic.main.key_value_list_item.view.*

class KeyValueAdapter(
    private val onItemClickListener: ((KeyValue) -> Unit)?,
    private val onDeleteClickListener: ((KeyValue) -> Unit)?
) :
    ListAdapter<KeyValue, KeyValueAdapter.KeyValueViewHolder>(
        DIFF_CALLBACK
    ) {
    private val viewBinderHelper = ViewBinderHelper()

    class KeyValueViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val mainLayout = rootView.mainLayout
        val key: TextView = rootView.key
        val value: TextView = rootView.value

        val swipeLayout = rootView.swipeLayout
        val delete = rootView.delete
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
            holder.mainLayout.setOnClickListener {
                it(keyValue)
            }
        }

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeLayout, keyValue.id.toString());
        viewBinderHelper.closeLayout(keyValue.id.toString())
        onDeleteClickListener?.let {
            holder.delete.setOnClickListener {
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
