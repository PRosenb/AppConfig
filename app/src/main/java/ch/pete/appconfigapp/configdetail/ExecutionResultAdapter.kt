package ch.pete.appconfigapp.configdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.model.ExecutionResult
import ch.pete.appconfigapp.model.ResultType

class ExecutionResultAdapter(
    private val onItemClickListener: ((ExecutionResult) -> Unit)?
) :
    ListAdapter<ExecutionResult, ExecutionResultAdapter.ConfigEntryViewHolder>(
        DIFF_CALLBACK
    ) {

    class ConfigEntryViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val executionResult: TextView = rootView.findViewById(R.id.executionResult)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConfigEntryViewHolder {
        val rootView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.execution_result_list_item, parent, false)
        return ConfigEntryViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ConfigEntryViewHolder, position: Int) {
        val executionResult = getItem(position)
        val context = holder.executionResult.context
        holder.executionResult.text =
            when (executionResult.resultType) {
                ResultType.SUCCESS -> {
                    // set color to default text color, same as resultTitle
                    holder.executionResult.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.black
                        )
                    )
                    context.resources.getQuantityString(
                        R.plurals.result_success,
                        executionResult.valuesCount,
                        executionResult.valuesCount
                    )
                }
                ResultType.ACCESS_DENIED -> {
                    holder.executionResult.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.failure_color
                        )
                    )
                    context.getText(R.string.result_access_denied)
                }
                ResultType.EXCEPTION -> {
                    holder.executionResult.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.failure_color
                        )
                    )
                    String.format(
                        context.getString(R.string.result_exception),
                        executionResult.message
                    )
                }
            }
        onItemClickListener?.let {
            holder.itemView.setOnClickListener {
                it(executionResult)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ExecutionResult>() {
                override fun areItemsTheSame(oldItem: ExecutionResult, newItem: ExecutionResult) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ExecutionResult,
                    newItem: ExecutionResult
                ) =
                    oldItem == newItem
            }
    }
}
