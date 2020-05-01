package ch.pete.appconfigapp.configlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.model.ConfigEntry
import ch.pete.appconfigapp.model.ResultType

class ConfigEntryAdapter(
    private val onItemClickListener: ((ConfigEntry) -> Unit)?,
    private val onExecuteClickListener: ((ConfigEntry) -> Unit)?
) :
    ListAdapter<ConfigEntry, ConfigEntryAdapter.ConfigEntryViewHolder>(
        DIFF_CALLBACK
    ) {

    class ConfigEntryViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val execute: ImageView = rootView.findViewById(R.id.execute)
        val title: TextView = rootView.findViewById(R.id.title)
        val keysCount: TextView = rootView.findViewById(R.id.keysCount)
        val resultTitle: TextView = rootView.findViewById(R.id.resultTitle)
        val lastResult: TextView = rootView.findViewById(R.id.lastResult)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConfigEntryViewHolder {
        val rootView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.config_entry_list_item, parent, false)
        return ConfigEntryViewHolder(
            rootView
        )
    }

    override fun onBindViewHolder(holder: ConfigEntryViewHolder, position: Int) {
        val configEntry = getItem(position)
        holder.title.text = configEntry.config.name
        val context = holder.keysCount.context
        holder.keysCount.text =
            context.resources.getQuantityString(
                R.plurals.keys_count,
                configEntry.keyValues.size,
                configEntry.keyValues.size
            )
        holder.lastResult.text =
            configEntry.executionResults.maxBy { it.timestamp }
                ?.let {
                    when (it.resultType) {
                        ResultType.SUCCESS -> {
                            // set color to default text color, same as resultTitle
                            holder.lastResult.setTextColor(holder.resultTitle.textColors)
                            context.resources.getQuantityString(
                                R.plurals.result_success,
                                it.valuesCount,
                                it.valuesCount
                            )
                        }
                        ResultType.ACCESS_DENIED -> {
                            holder.lastResult.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.failure_color
                                )
                            )
                            context.getText(R.string.result_access_denied)
                        }
                        ResultType.EXCEPTION -> {
                            holder.lastResult.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.failure_color
                                )
                            )
                            String.format(context.getString(R.string.result_exception), it.message)
                        }
                    }
                } ?: ""
        onExecuteClickListener?.let {
            holder.execute.setOnClickListener {
                it(configEntry)
            }
        }
        onItemClickListener?.let {
            holder.itemView.setOnClickListener {
                it(configEntry)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ConfigEntry>() {
                override fun areItemsTheSame(oldItem: ConfigEntry, newItem: ConfigEntry) =
                    oldItem.config.id == newItem.config.id

                override fun areContentsTheSame(oldItem: ConfigEntry, newItem: ConfigEntry) =
                    oldItem == newItem
            }
    }
}
