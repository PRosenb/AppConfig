package ch.pete.appconfigapp.configlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ch.pete.appconfigapp.AppConfigViewModel
import ch.pete.appconfigapp.R
import kotlinx.android.synthetic.main.fragment_config_list.view.*

class ConfigListFragment : Fragment(), ConfigListView {
    private val viewModel: AppConfigViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.configListView = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_config_list, container, false)
        val adapter = ConfigEntryAdapter(
            onExecuteClickListener = {
                viewModel.onExecuteClicked(it)
            },
            onItemClickListener = {
                viewModel.onConfigEntryClicked(it)
            }
        )
        viewModel.configEntries.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        rootView.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            this.adapter = adapter
        }
        return rootView
    }
}
