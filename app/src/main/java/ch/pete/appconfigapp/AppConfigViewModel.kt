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

    fun configEntryById(configId: Long?): LiveData<ConfigEntry>? {
        return configId?.let {
            appConfigDao.fetchConfigEntryByIdAsLiveData(configId)
        }
    }

    fun configById(configId: Long): LiveData<Config> =
        appConfigDao.fetchConfigById(configId)

    fun executionResultEntriesByConfigId(configId: Long) =
        appConfigDao.fetchExecutionResultEntriesByConfigId(configId)

    fun keyValueEntriesByConfigId(configId: Long) =
        appConfigDao.keyValueEntriesByConfigId(configId)

    fun keyValueEntryByKeyValueId(keyValueId: Long) =
        appConfigDao.keyValueEntryByKeyValueId(keyValueId)

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

    fun onAddConfigClicked() {
        viewModelScope.launch {
            val configId = withContext(Dispatchers.IO) {
                appConfigDao.insertEmptyConfig()
            }
            mainView.showDetails(configId)
        }
    }

    fun onConfigEntryClicked(configEntry: ConfigEntry) {
        configEntry.config.id?.let {
            mainView.showDetails(it)
        } ?: throw IllegalArgumentException("config.id is null")
    }

    fun onConfigEntryDeleteClicked(configEntry: ConfigEntry) {
        viewModelScope.launch {
            appConfigDao.deleteConfigEntry(configEntry)
        }
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
        configDetailView.showKeyValueDetails(configId, null)
    }

    fun onKeyValueEntryClicked(keyValue: KeyValue) {
        configDetailView.showKeyValueDetails(keyValue.configId, keyValue.id)
    }

    fun onKeyValueDeleteClicked(keyValue: KeyValue) {
        viewModelScope.launch {
            appConfigDao.deleteKeyValue(keyValue)
        }
    }

    fun storeKeyValue(keyValue: KeyValue) {
        viewModelScope.launch {
            if (keyValue.id == null) {
                appConfigDao.insertKeyValue(keyValue)
            } else {
                appConfigDao.updateKeyValue(keyValue)
            }
        }
    }

    fun onExecutionResultEntryClicked(executionResult: ExecutionResult) {

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
                resultType = ResultType.SUCCESS,
                valuesCount = appliedValuesCount
            )
        } catch (e: SecurityException) {
            addExecutionResult(
                configEntry = configEntry,
                resultType = ResultType.ACCESS_DENIED
            )
        } catch (e: RuntimeException) {
            addExecutionResult(
                configEntry = configEntry,
                resultType = ResultType.EXCEPTION,
                message = e.message
            )
        }
    }

    private suspend fun addExecutionResult(
        configEntry: ConfigEntry,
        resultType: ResultType,
        valuesCount: Int = 0,
        message: String? = null
    ) {
        val executionResult = ExecutionResult(
            configId = configEntry.config.id
                ?: throw IllegalArgumentException("config.id must not be null"),
            resultType = resultType,
            valuesCount = valuesCount,
            message = message
        )
        appConfigDatabase.appConfigDao().insertExecutionResult(listOf(executionResult))
    }
}
