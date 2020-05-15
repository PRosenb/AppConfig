package ch.pete.appconfigapp.configdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.pete.appconfigapp.MainActivityViewModel
import ch.pete.appconfigapp.db.AppConfigDao
import ch.pete.appconfigapp.model.KeyValue
import kotlinx.coroutines.launch

class KeyValueViewModel : ViewModel() {
    lateinit var mainActivityViewModel: MainActivityViewModel

    private val appConfigDao: AppConfigDao by lazy {
        mainActivityViewModel.appConfigDatabase.appConfigDao()
    }

    fun keyValueEntryByKeyValueId(keyValueId: Long) =
        appConfigDao.keyValueEntryByKeyValueId(keyValueId)

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
