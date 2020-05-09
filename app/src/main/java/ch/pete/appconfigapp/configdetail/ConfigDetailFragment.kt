package ch.pete.appconfigapp.configdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.pete.appconfigapp.AppConfigViewModel
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.model.ConfigEntry
import kotlinx.android.synthetic.main.fragment_config_detail.*
import kotlinx.android.synthetic.main.fragment_config_detail.view.*

class ConfigDetailFragment : Fragment(), ConfigDetailView {
    companion object {
        private const val PREF_AUTHORITY = "authority"
        const val ARG_CONFIG_ENTRY_ID = "ARG_CONFIG_ENTRY_ID"
    }

    private val viewModel: AppConfigViewModel by activityViewModels()
    private var configEntry: ConfigEntry? = null

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
        rootView.button.setOnClickListener {
            configEntry?.let {
                val updatedConfigEntry = it.copy(
                    config = it.config.copy(
                        name = name.text.toString(),
                        authority = authority.text.toString()
                    )
                )
                viewModel.updateConfigEntry(updatedConfigEntry.config)
                // TODO update values too
                viewModel.onDetailExecuteClicked(updatedConfigEntry)
            }
            // TODO handle error
        }

        val configId = arguments?.let {
            if (it.containsKey(ARG_CONFIG_ENTRY_ID)) {
                it.getLong(ARG_CONFIG_ENTRY_ID)
            } else {
                null
            }
        }

        if (configId != null) {
            viewModel.configEntryById(configId)
                ?.observe(viewLifecycleOwner, Observer { loadedConfigEntry ->
                    configEntry = loadedConfigEntry
                    name.setText(loadedConfigEntry.config.name)
                    authority.setText(loadedConfigEntry.config.authority)
                })
            // TODO handle not found

            initExecutionResultView(configId, rootView)
            initKeyValuesView(configId, rootView)

            rootView.addKeyValueButton.setOnClickListener {
                viewModel.onAddKeyValueClicked(configId)
            }
        }

        return rootView
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

    override fun onResume() {
        super.onResume()
        loadUiValues()
    }

    override fun onPause() {
        storeUiValues()
        super.onPause()
    }

    private fun storeUiValues() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(PREF_AUTHORITY, authority.text.toString())
        editor.apply()
    }

    private fun loadUiValues() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.getString(PREF_AUTHORITY, null)?.let {
            authority.setText(it)
        }
    }
}
