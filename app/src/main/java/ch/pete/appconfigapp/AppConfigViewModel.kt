package ch.pete.appconfigapp

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import timber.log.Timber

class AppConfigViewModel(application: Application) : AndroidViewModel(application) {
    data class ConfigEntry(val name: String, val authority: String, val values: String)

    lateinit var mainView: MainView

    fun onButtonClicked(authority: String, values: String) {
        val contentValues = convertCommaSeparatedValuesToContentValues(values)
        callContentProviderAndShowResult(contentValues, authority)
    }

    private fun convertCommaSeparatedValuesToContentValues(valuesStr: String): ContentValues {
        val keyWithValueList = valuesStr.split(",")
        val keyValuePairList = keyWithValueList.mapNotNull {
            val splitKeyValue = it.split("=")
            if (splitKeyValue.size > 1) {
                splitKeyValue[0].trim() to splitKeyValue[1].trim()
            } else {
                null
            }
        }
        val contentValues = ContentValues()
        keyValuePairList.forEach {
            contentValues.put(it.first, it.second)
        }
        return contentValues
    }

    private fun callContentProviderAndShowResult(values: ContentValues, authority: String) {
        val authorityUri = Uri.parse("content://$authority")
        try {
            val appliedValuesCount = getApplication<Application>().contentResolver.update(
                authorityUri,
                values,
                null,
                null
            )
            mainView.appendToOutput(
                getApplication<Application>().resources.getQuantityString(
                    R.plurals.success,
                    appliedValuesCount,
                    appliedValuesCount
                )
            )
        } catch (e: SecurityException) {
            mainView.appendToOutput(
                String.format(
                    getApplication<Application>().getString(R.string.failure),
                    "Access denied"
                )
            )
            Timber.d("SecurityException", e)
        } catch (e: RuntimeException) {
            mainView.appendToOutput(
                String.format(
                    getApplication<Application>().getString(R.string.failure),
                    e.message
                )
            )
        }
    }

}
