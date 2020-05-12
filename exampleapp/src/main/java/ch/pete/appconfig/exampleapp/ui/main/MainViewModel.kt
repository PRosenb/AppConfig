package ch.pete.appconfig.exampleapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val sharedPreferencesLiveData = SharedPreferencesLiveData(
        PreferenceManager.getDefaultSharedPreferences(application)
    )
}
