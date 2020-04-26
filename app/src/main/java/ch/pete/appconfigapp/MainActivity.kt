package ch.pete.appconfigapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity(), MainView {
    companion object {
        private const val PREF_AUTHORITY = "authority"
        private const val PREF_VALUES = "values"
    }

    private lateinit var viewModel: AppConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(AppConfigViewModel::class.java)
        viewModel.mainView = this

        button.setOnClickListener {
            viewModel.onButtonClicked(authority.text.toString(), values.text.toString())
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

    @SuppressLint("SetTextI18n")
    override fun appendToOutput(text: String) {
        output.text = "${output.text}$text"
    }

    private fun storeUiValues() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString(PREF_AUTHORITY, authority.text.toString())
        editor.putString(PREF_VALUES, values.text.toString())
        editor.apply()
    }

    private fun loadUiValues() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.getString(PREF_AUTHORITY, null)?.let {
            authority.setText(it)
        }
        prefs.getString(PREF_VALUES, null)?.let {
            values.setText(it)
        }
    }
}
