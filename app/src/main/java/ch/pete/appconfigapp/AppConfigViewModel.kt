package ch.pete.appconfigapp

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ch.pete.appconfigapp.configdetail.ConfigDetailView
import ch.pete.appconfigapp.configlist.ConfigListView
import ch.pete.appconfigapp.db.DatabaseBuilder
import ch.pete.appconfigapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val appConfigDatabase = DatabaseBuilder.builder(application).build()
    private val appConfigDao = appConfigDatabase.appConfigDao()

    val configEntries = appConfigDao.fetchConfigEntries()

    lateinit var mainView: MainView
    lateinit var configListView: ConfigListView
    lateinit var configDetailView: ConfigDetailView

    fun initViewModel() {
        viewModelScope.launch {
            insertTestValues()
        }
    }

    fun configEntryById(configId: Long?): LiveData<ConfigEntry>? {
        return configId?.let {
            appConfigDao.fetchConfigEntryByIdAsLiveData(configId)
        }
    }

    fun configById(configId: Long?): LiveData<Config>? {
        return configId?.let {
            appConfigDao.fetchConfigById(configId)
        }
    }

    fun executionResultEntriesByConfigId(configId: Long) =
        appConfigDao.fetchExecutionResultEntriesByConfigId(configId)

    fun keyValueEntriesByConfigId(configId: Long) =
        appConfigDao.keyValueEntriesByConfigId(configId)

    fun onNameUpdated(name: String, configId: Long) {
        viewModelScope.launch {
            appConfigDao.updateConfigName(name, configId)
        }
    }

    fun onAuthorityUpdated(authority: String, configId: Long) {
        viewModelScope.launch {
            appConfigDao.updateConfigAuthority(authority, configId)
        }
    }

    private suspend fun insertTestValues() {
        appConfigDao.insertConfigEntry(
            ConfigEntry(
                config = Config(
                    name = "Successful with two keys",
                    authority = "com.trabr.provider.config"
                ),
                keyValues = listOf(
                    KeyValue(
                        key = "key0",
                        value = "value0"
                    ),
                    KeyValue(
                        key = "key1",
                        value = "value1"
                    )
                ),
                executionResults = emptyList()
            )
        )
        appConfigDao.insertConfigEntry(
            ConfigEntry(
                config = Config(
                    name = "No access test",
                    authority = "com.trabr.provider.config"
                ),
                keyValues = listOf(
                    KeyValue(
                        key = "TEST_ACCESS_DENIED",
                        value = "true"
                    )
                ),
                executionResults = emptyList()
            )
        )
        appConfigDao.insertConfigEntry(
            ConfigEntry(
                config = Config(
                    name = "Wrong authority",
                    authority = "com.trabr.provider.config1"
                ),
                keyValues = listOf(
                    KeyValue(
                        key = "key0",
                        value = "value0"
                    ),
                    KeyValue(
                        key = "key1",
                        value = "value1"
                    )
                ),
                executionResults = emptyList()
            )
        )
    }

    fun onAddConfigClicked() {
        viewModelScope.launch {
            val configId = withContext(Dispatchers.IO) {
                appConfigDao.insertConfig(Config(name = "", authority = ""))
            }
            mainView.showDetails(configId)
        }
    }

    fun onConfigEntryClicked(configEntry: ConfigEntry) {
        configEntry.config.id?.let {
            mainView.showDetails(it)
        } ?: throw IllegalArgumentException("config.id is null")
    }

    fun onExecuteClicked(configEntry: ConfigEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                callContentProviderAndShowResult(configEntry)
            }
        }
    }

    fun onDetailExecuteClicked(configId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val configEntry = appConfigDao.fetchConfigEntryById(configId)
                if (configEntry != null) {
                    callContentProviderAndShowResult(configEntry)
                } else {
                    // TODO handle error
                }
            }
        }
    }

    fun onAddKeyValueClicked(configId: Long) {

    }

    fun onExecutionResultEntryClicked(executionResult: ExecutionResult) {

    }

    fun onKeyValueEntryClicked(keyValue: KeyValue) {

    }

    private suspend fun callContentProviderAndShowResult(configEntry: ConfigEntry) {
        val contentValues = configEntry.keyValues
            .fold(ContentValues()) { contentValues, keyValue ->
                contentValues.put(keyValue.key, keyValue.value)
                contentValues
            }

        val authorityUri = Uri.parse("content://${configEntry.config.authority}")
        try {
            val appliedValuesCount = getApplication<Application>().contentResolver.update(
                authorityUri,
                contentValues,
                null,
                null
            )

            addExecutionResult(
                configEntry,
                ExecutionResult(
                    resultType = ResultType.SUCCESS,
                    valuesCount = appliedValuesCount
                )
            )
        } catch (e: SecurityException) {
            addExecutionResult(
                configEntry = configEntry,
                executionResult = ExecutionResult(resultType = ResultType.ACCESS_DENIED)
            )
        } catch (e: RuntimeException) {
            addExecutionResult(
                configEntry = configEntry,
                executionResult = ExecutionResult(
                    resultType = ResultType.EXCEPTION,
                    message = e.message
                )
            )
        }
    }

    private suspend fun addExecutionResult(
        configEntry: ConfigEntry,
        executionResult: ExecutionResult
    ) {
        executionResult.configId =
            configEntry.config.id ?: throw IllegalArgumentException("config.id must not be null")
        appConfigDatabase.appConfigDao().insertExecutionResult(listOf(executionResult))
    }
}
