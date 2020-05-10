package ch.pete.appconfigapp.configdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.pete.appconfigapp.AppConfigViewModel
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.model.Config
import kotlinx.android.synthetic.main.fragment_config_detail.*
import kotlinx.android.synthetic.main.fragment_config_detail.view.*

class ConfigDetailFragment : Fragment(), ConfigDetailView {
    companion object {
        const val ARG_CONFIG_ENTRY_ID = "ARG_CONFIG_ENTRY_ID"
    }

    private val viewModel: AppConfigViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.configDetailView = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_config_detail, container, false)

        arguments?.let {
            if (it.containsKey(ARG_CONFIG_ENTRY_ID)) {
                val configId = it.getLong(ARG_CONFIG_ENTRY_ID)
                initView(rootView, configId)
            } else {
                parentFragmentManager.popBackStack()
            }
        } ?: parentFragmentManager.popBackStack()
        return rootView
    }

    private fun initView(rootView: View, configId: Long) {
        val configLiveData = viewModel.configById(configId)
        configLiveData.observe(viewLifecycleOwner, object : Observer<Config> {
            override fun onChanged(loadedConfig: Config) {
                configLiveData.removeObserver(this)
                name.setText(loadedConfig.name)
                authority.setText(loadedConfig.authority)
            }
        })

        rootView.execute.setOnClickListener {
            viewModel.onDetailExecuteClicked(configId)
        }

        initExecutionResultView(configId, rootView)
        initKeyValuesView(configId, rootView)

        rootView.addKeyValueButton.setOnClickListener {
            viewModel.onAddKeyValueClicked(configId)
        }

        rootView.name.addTextChangedListener(
            afterTextChanged = {
                viewModel.onNameUpdated(it.toString(), configId)
            }
        )
        rootView.authority.addTextChangedListener(
            afterTextChanged = {
                viewModel.onAuthorityUpdated(it.toString(), configId)
            }
        )
    }

    private fun initExecutionResultView(configId: Long, rootView: View) {
        val executionResultAdapter = ExecutionResultAdapter(
            onItemClickListener = {
                viewModel.onExecutionResultEntryClicked(it)
            }
        ).apply {
            registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        rootView.executionResults.layoutManager?.scrollToPosition(0)
                    }
                })
        }
        viewModel.executionResultEntriesByConfigId(configId)
            .observe(viewLifecycleOwner, Observer {
                executionResultAdapter.submitList(it)
            })
        rootView.executionResults.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            this.adapter = executionResultAdapter
        }
    }

    private fun initKeyValuesView(configId: Long, rootView: View) {
        val adapter = KeyValueAdapter(
            onItemClickListener = {
                viewModel.onKeyValueEntryClicked(it)
            }
        )
        viewModel.keyValueEntriesByConfigId(configId)
            .observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        rootView.keyValues.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            this.adapter = adapter
        }
    }

    override fun showKeyValueDetails(configId: Long, keyValueId: Long?) {
        val keyValueDialogFragment = KeyValueDialogFragment()
        val args = Bundle()
        args.putLong(KeyValueDialogFragment.ARG_CONFIG_ID, configId)
        keyValueId?.let { args.putLong(KeyValueDialogFragment.ARG_KEY_VALUE_ID, it) }
        keyValueDialogFragment.arguments = args
        keyValueDialogFragment.show(parentFragmentManager, "keyValueDialogFragment")
    }
}
