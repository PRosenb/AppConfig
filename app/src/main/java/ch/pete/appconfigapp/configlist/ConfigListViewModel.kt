package ch.pete.appconfigapp.configlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ch.pete.appconfigapp.MainActivityViewModel
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.db.AppConfigDao
import ch.pete.appconfigapp.model.ConfigEntry
import ch.pete.appconfigapp.model.KeyValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ConfigListViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var view: ConfigListView
    lateinit var mainActivityViewModel: MainActivityViewModel

    private val appConfigDao: AppConfigDao by lazy {
        mainActivityViewModel.appConfigDatabase.appConfigDao()
    }
    val configEntries: LiveData<List<ConfigEntry>> by lazy {
        appConfigDao.fetchConfigEntries()
    }

    fun keyValueEntryByKeyValueId(keyValueId: Long) =
        appConfigDao.keyValueEntryByKeyValueId(keyValueId)

    fun onAddConfigClicked() {
        viewModelScope.launch {
            val configId = withContext(Dispatchers.IO) {
                appConfigDao.insertEmptyConfig()
            }
            view.showDetails(configId)
        }
    }

    fun onConfigEntryClicked(configEntry: ConfigEntry) {
        configEntry.config.id?.let {
            view.showDetails(it)
        } ?: throw IllegalArgumentException("config.id is null")
    }

    fun onConfigEntryCloneClicked(configEntry: ConfigEntry) {
        viewModelScope.launch {
            appConfigDao.cloneConfigEntryWithoutResults(
                configEntry,
                String.format(
                    getApplication<Application>().getString(R.string.cloned_name),
                    configEntry.config.name
                )
            )
        }
    }

    fun onConfigEntryDeleteClicked(configEntry: ConfigEntry) {
        viewModelScope.launch {
            appConfigDao.deleteConfigEntry(configEntry)
        }
    }

    fun onExecuteClicked(configEntry: ConfigEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mainActivityViewModel.callContentProviderAndShowResult(configEntry)
            }
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
}
