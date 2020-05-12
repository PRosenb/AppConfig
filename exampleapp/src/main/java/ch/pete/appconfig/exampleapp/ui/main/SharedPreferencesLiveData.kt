package ch.pete.appconfig.exampleapp.ui.main

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.lifecycle.LiveData


class SharedPreferencesLiveData(
    var sharedPrefs: SharedPreferences
) :
    LiveData<Map<String, *>>() {
    private val preferenceChangeListener =
        OnSharedPreferenceChangeListener { _, _ ->
            value = sharedPrefs.all
        }

    override fun onActive() {
        super.onActive()
        value = sharedPrefs.all
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}
