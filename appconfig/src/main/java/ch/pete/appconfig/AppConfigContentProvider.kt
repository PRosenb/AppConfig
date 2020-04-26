package ch.pete.appconfig

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import androidx.preference.PreferenceManager
import ch.pete.appconfig.util.SignatureUtils
import timber.log.Timber

class AppConfigContentProvider : ContentProvider() {
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

            val allowedApp = AppConfig.authorizedApps.firstOrNull {
                callingApplicationId == it.applicationId
                        && callingSignature == it.signature
            }

            allowedApp ?: throw SecurityException("Access Denied")
            if (values?.containsKey("TEST_ACCESS_DENIED") == true) {
                throw SecurityException("Access Denied")
            } else {
                Timber.d("authorized access to $allowedApp")
            }

            var appliedKeysCount = 0
            values?.let {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = prefs.edit()
                values.keySet()
                    .filter {
                        if (AppConfig.authorizedKeys.isEmpty()) {
                            true // allow all if authorizedKeys is empty
                        } else {
                            AppConfig.authorizedKeys.contains(it)
                        }
                    }
                    .forEach { key ->
                        val value = values.get(key)
                        when (val dataType = value.javaClass) {
                            String::class.java -> editor.putString(key, value as String)
                            Boolean::class.java -> editor.putBoolean(key, value as Boolean)
                            else -> throw IllegalArgumentException("Unsupported data type $dataType")
                        }
                        appliedKeysCount++
                    }
                editor.apply()
            }

            return appliedKeysCount
        } ?: throw IllegalArgumentException("context is null")
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
