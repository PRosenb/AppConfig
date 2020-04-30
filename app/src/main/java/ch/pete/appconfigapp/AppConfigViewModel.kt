package ch.pete.appconfigapp

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

enum class ResultType {
    SUCCESS, ACCESS_DENIED, EXCEPTION
}

data class Result(
    val resultType: ResultType,
    val valuesCount: Int = 0,
    val message: String? = null
)

data class ConfigEntry(
    val name: String,
    val authority: String,
    val values: Map<String, String>,
    val lastResult: Result? = null
)

class AppConfigViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var mainView: MainView
    val configEntries = MutableLiveData<List<ConfigEntry>>()

    init {
        configEntries.value = listOf(
            ConfigEntry(
                name = "String 1",
                authority = "com.trabr.provider.config",
                values = mapOf("key0" to "value0")
            ),
            ConfigEntry(
                name = "String 2",
                authority = "com.trabr.provider.config",
                values = mapOf("TEST_ACCESS_DENIED" to "value1", "key2" to "value2")
            ),
            ConfigEntry(
                name = "String 3",
                authority = "com.trabr.provider.config1",
                values = mapOf("key1" to "value1", "key2" to "value2")
            )
        )
    }

    fun onButtonClicked(authority: String, values: String) {
    }

    fun onConfigEntryClicked(configEntry: ConfigEntry) {

    }

    fun onExecuteClicked(configEntry: ConfigEntry) {
        callContentProviderAndShowResult(configEntry)
    }

    private fun callContentProviderAndShowResult(configEntry: ConfigEntry) {
        val contentValues = configEntry.values.entries
            .fold(ContentValues()) { contentValues, entry ->
                contentValues.put(entry.key, entry.value)
                contentValues
            }

        val authorityUri = Uri.parse("content://${configEntry.authority}")
        try {
            val appliedValuesCount = getApplication<Application>().contentResolver.update(
                authorityUri,
                contentValues,
                null,
                null
            )

            updateConfigEntries(
                configEntry = configEntry,
                result = Result(resultType = ResultType.SUCCESS, valuesCount = appliedValuesCount)
            )
        } catch (e: SecurityException) {
            updateConfigEntries(
                configEntry = configEntry,
                result = Result(resultType = ResultType.ACCESS_DENIED)
            )
        } catch (e: RuntimeException) {
            updateConfigEntries(
                configEntry = configEntry,
                result = Result(resultType = ResultType.EXCEPTION, message = e.message)
            )
        }
    }

    private fun updateConfigEntries(configEntry: ConfigEntry, result: Result) {
        configEntries.value = configEntries.value?.let { values ->
            values.toMutableList().apply {
                set(
                    values.indexOf(configEntry),
                    configEntry.copy(
                        lastResult = result
                    )
                )
            }
        }
    }
}
