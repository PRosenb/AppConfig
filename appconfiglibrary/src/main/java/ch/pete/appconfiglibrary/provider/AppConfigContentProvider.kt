package ch.pete.appconfiglibrary.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import androidx.preference.PreferenceManager
import ch.pete.appconfiglibrary.SignatureUtils
import timber.log.Timber

class AppConfigContentProvider : ContentProvider() {
    private data class AllowedApp(val applicationId: String, val signature: String)

    private val allowedApps = listOf(
        AllowedApp(
            applicationId = "ch.pete.appconfig",
            signature = "06:02:0D:0D:0E:0D:05:07:0A:00:0E:03:0D:06:03:00:02:0B:06:07:07:02:03:06:0E:0B:08:07:08:0B:07:01"
        ),
        AllowedApp(
            applicationId = "ch.pete.appconfig",
            signature = "0F:00:04:0F:09:08:09:01:00:06:02:0C:03:07:05:03:00:01:04:09:03:05:02:06:0B:01:00:00:05:0E:0F:02"
        )
    )

    override fun onCreate() = true

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?, selectionArgs: Array<out String>?
    ): Int {
        Timber.d("uri: $uri, values: $values")
        context?.let { context ->
            val callingApplicationId = context.packageManager.getNameForUid(Binder.getCallingUid())
            val callingSignature = SignatureUtils.getSignatureHash(context, callingApplicationId)
            Timber.d("callingApp: $callingApplicationId, callingAppSignature: $callingSignature")

            val allowedApp = allowedApps.firstOrNull {
                callingApplicationId == it.applicationId
                        && callingSignature == it.signature
            }

            allowedApp ?: throw SecurityException("Access Denied")
            if (values?.containsKey("TEST_ACCESS_DENIED") == true) {
                throw SecurityException("Access Denied")
            } else {
                Timber.d("authorized access to $allowedApp")
            }

            values?.let {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = prefs.edit()
                values.keySet().forEach { key ->
                    val value = values.get(key)
                    when (val dataType = value.javaClass) {
                        String::class.java -> editor.putString(key, value as String)
                        Boolean::class.java -> editor.putBoolean(key, value as Boolean)
                        else -> throw IllegalArgumentException("Unsupported data type $dataType")
                    }
                }
                editor.apply()
            }
        } ?: throw IllegalArgumentException("context is null")

        return values?.size() ?: 0
    }

    override fun getType(uri: Uri): String? = ""

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?, selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? =
        throw UnsupportedOperationException()

    override fun insert(uri: Uri, values: ContentValues?): Uri? =
        throw UnsupportedOperationException()

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException()
}
