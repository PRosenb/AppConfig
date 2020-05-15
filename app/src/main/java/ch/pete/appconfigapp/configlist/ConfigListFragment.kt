package ch.pete.appconfigapp.configlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ch.pete.appconfigapp.MainActivityViewModel
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.configdetail.ConfigDetailFragment
import kotlinx.android.synthetic.main.fragment_config_list.view.addConfigButton
import kotlinx.android.synthetic.main.fragment_config_list.view.recyclerView

@Suppress("unused")
class ConfigListFragment : Fragment(), ConfigListView {
    private val viewModel: ConfigListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.view = this
        viewModel.mainActivityViewModel =
            ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
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
            },
            onCloneClickListener = {
                viewModel.onConfigEntryCloneClicked(it)
            },
            onDeleteClickListener = {
                viewModel.onConfigEntryDeleteClicked(it)
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

        rootView.addConfigButton.setOnClickListener {
            viewModel.onAddConfigClicked()
        }

        return rootView
    }

    override fun showDetails(configId: Long) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val fragment = ConfigDetailFragment()

        fragment.arguments = Bundle().apply {
            putLong(ConfigDetailFragment.ARG_CONFIG_ENTRY_ID, configId)
        }
        fragmentTransaction
            .replace(
                R.id.fragmentContainer,
                fragment
            )

        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
