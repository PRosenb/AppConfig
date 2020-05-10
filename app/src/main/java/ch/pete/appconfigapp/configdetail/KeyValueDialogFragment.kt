package ch.pete.appconfigapp.configdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ch.pete.appconfigapp.AppConfigViewModel
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.model.KeyValue
import kotlinx.android.synthetic.main.dialogfragment_keyvalues.view.*


class KeyValueDialogFragment : DialogFragment() {
    companion object {
        const val ARG_CONFIG_ID = "config_id"
        const val ARG_KEY_VALUE_ID = "keyValue_id"
    }

    private val viewModel: AppConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialogfragment_keyvalues, container, false)
        arguments?.let {
            val configId = if (it.containsKey(ARG_CONFIG_ID)) {
                it.getLong(ARG_CONFIG_ID)
            } else {
                null
            }
            if (configId != null) {
                if (it.containsKey(ARG_KEY_VALUE_ID)) {
                    val keyValueLiveData =
                        viewModel.keyValueEntryByKeyValueId(it.getLong(ARG_KEY_VALUE_ID))
                    keyValueLiveData.observe(viewLifecycleOwner, object : Observer<KeyValue> {
                        override fun onChanged(keyValue: KeyValue) {
                            keyValueLiveData.removeObserver(this)
                            rootView.key.setText(keyValue.key)
                            rootView.value.setText(keyValue.value)
                        }
                    })

                }

                rootView.ok.setOnClickListener {
                    val keyValue = KeyValue(
                        id = if (arguments?.containsKey(ARG_KEY_VALUE_ID) == true) {
                            arguments?.getLong(ARG_KEY_VALUE_ID)
                        } else {
                            null
                        },
                        configId = configId,
                        key = rootView.key.text.toString(),
                        value = rootView.value.text.toString()
                    )
                    viewModel.storeKeyValue(keyValue)
                    dialog?.dismiss()
                }
            } else {
                parentFragmentManager.popBackStack()
            }
        } ?: parentFragmentManager.popBackStack()

        return rootView
    }
}
