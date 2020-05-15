package ch.pete.appconfigapp.configdetail

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ch.pete.appconfigapp.MainActivityViewModel
import ch.pete.appconfigapp.R
import ch.pete.appconfigapp.db.AppConfigDao
import ch.pete.appconfigapp.model.Config
import ch.pete.appconfigapp.model.KeyValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ConfigDetailViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var view: ConfigDetailView
    lateinit var mainActivityViewModel: MainActivityViewModel

    private val appConfigDao: AppConfigDao by lazy {
        mainActivityViewModel.appConfigDatabase.appConfigDao()
    }

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

    fun configById(configId: Long): LiveData<Config> =
        appConfigDao.fetchConfigById(configId)

    fun executionResultEntriesByConfigId(configId: Long) =
        appConfigDao.fetchExecutionResultEntriesByConfigId(configId)

    fun keyValueEntriesByConfigId(configId: Long) =
        appConfigDao.keyValueEntriesByConfigId(configId)

    fun onDetailExecuteClicked(configId: Long) {
        viewModelScope.launch {
            val foundItem = withContext(Dispatchers.IO) {
                val configEntry = appConfigDao.fetchConfigEntryById(configId)
                if (configEntry != null) {
                    mainActivityViewModel.callContentProviderAndShowResult(configEntry)
                    true
                } else {
                    Timber.e("ConfigEntry with id '$configId' not found.")
                    false
                }
            }
            if (!foundItem) {
                Toast.makeText(getApplication(), R.string.error_occurred, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onAddKeyValueClicked(configId: Long) {
        view.showKeyValueDetails(configId, null)
    }

    fun onKeyValueEntryClicked(keyValue: KeyValue) {
        view.showKeyValueDetails(keyValue.configId, keyValue.id)
    }

    fun onKeyValueDeleteClicked(keyValue: KeyValue) {
        viewModelScope.launch {
            appConfigDao.deleteKeyValue(keyValue)
        }
    }
}
