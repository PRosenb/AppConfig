package ch.pete.appconfig

import android.content.ContentValues

data class AuthorizedApp(
    /**
     * The applicationId of the calling app.
     */
    val applicationId: String,
    /**
     * The SHA256 hash of the key the calling app is signed with.
     */
    val signature: String
)

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
        // AppConfigApp signed with key controlled solely by App Signing by Google Play
        authorizedApps.add(
            AuthorizedApp(
                applicationId = "ch.pete.appconfigapp",
                signature =
                "19:C6:22:9A:42:45:70:15:F5:D5:B7:5B:73:4C:DA:31:33:A5:6C:33:A8:72:A4:C5:D2:68:95:90:14:F6:33:CE"
            )
        )
    }
}
