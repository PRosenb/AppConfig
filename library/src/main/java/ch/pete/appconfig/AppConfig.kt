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
        @Suppress("MaxLineLength")
        authorizedApps.add(
            AuthorizedApp(
                applicationId = "ch.pete.appconfigapp",
                signature = "07:0D:00:06:0D:0F:02:0D:0C:0C:05:04:05:00:03:07:00:0A:0F:0D:07:0E:0B:01:01:0C:0F:0B:00:07:09:0C"
            )
        )
    }
}
