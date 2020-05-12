package ch.pete.appconfig.exampleapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ch.pete.appconfig.exampleapp.R
import kotlinx.android.synthetic.main.main_fragment.view.output

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.main_fragment, container, false)
        viewModel.sharedPreferencesLiveData.observe(viewLifecycleOwner, Observer { keyValueMap ->
            rootView.output.text =
                keyValueMap.keys.joinToString(separator = "") { "$it: ${keyValueMap[it]}\n" }
        })
        return rootView
    }
}
