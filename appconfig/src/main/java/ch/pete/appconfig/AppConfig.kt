package ch.pete.appconfig

import android.content.ContentValues

data class AuthorizedApp(val applicationId: String, val signature: String)

object AppConfig {
    /**
     * Empty list allows no apps.
     */
    @JvmStatic
    val authorizedApps = mutableListOf<AuthorizedApp>()

    /**
     * A listener to be called on the main thread whenever app config is received.
     */
    @JvmStatic
    var appConfigListener: ((ContentValues) -> Unit)? = null

    /**
     * Controls if the received key/values pairs are stored to shared preferences.
     */
    @JvmStatic
    var storeValuesToSharedPreferences = true

    /**
     * Controls which keys are stored to shared preferences. See also 'storeValuesToSharedPreferences'.
     * Empty lists allows all keys.
     */
    @JvmStatic
    val authorizedKeys = mutableListOf<String>()

    init {
        authorizedApps.add(
            AuthorizedApp(
                applicationId = "ch.pete.appconfigapp",
                signature = "06:02:0D:0D:0E:0D:05:07:0A:00:0E:03:0D:06:03:00:02:0B:06:07:07:02:03:06:0E:0B:08:07:08:0B:07:01"
            )
        )
        authorizedApps.add(
            AuthorizedApp(
                applicationId = "ch.pete.appconfigapp",
                signature = "0F:00:04:0F:09:08:09:01:00:06:02:0C:03:07:05:03:00:01:04:09:03:05:02:06:0B:01:00:00:05:0E:0F:02"
            )
        )
    }
}
