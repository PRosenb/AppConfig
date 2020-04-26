package ch.pete.testconfigapp

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    companion object {
        private const val PREF_AUTHORITY = "authority"
        private const val PREF_VALUES = "values"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            onButtonClicked()
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

    private fun onButtonClicked() {
        val contentValues = convertCommaSeparatedValuesToContentValues(values.text.toString())
        callContentProviderAndShowResult(contentValues, authority.text.toString())
    }

    private fun convertCommaSeparatedValuesToContentValues(valuesStr: String): ContentValues {
        val keyWithValueList = valuesStr.split(",")
        val keyValuePairList = keyWithValueList.map {
            val splitKeyValue = it.split("=")
            if (splitKeyValue.size > 1) {
                splitKeyValue[0].trim() to splitKeyValue[1].trim()
            } else {
                null
            }
        }.filterNotNull()
        val contentValues = ContentValues()
        keyValuePairList.forEach {
            contentValues.put(it.first, it.second)
        }
        return contentValues
    }

    private fun callContentProviderAndShowResult(values: ContentValues, authority: String) {
        val authorityUri = Uri.parse("content://$authority")
        try {
            val appliedValuesCount = contentResolver.update(authorityUri, values, null, null)
            output.text = resources.getQuantityString(
                    R.plurals.success,
                    appliedValuesCount,
                    output.text.toString(),
                    appliedValuesCount
            )
        } catch (e: SecurityException) {
            output.text = String.format(getString(R.string.failure), output.text.toString(), "Access denied")
        } catch (e: RuntimeException) {
            output.text = String.format(getString(R.string.failure), output.text.toString(), e.message)
        }
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
