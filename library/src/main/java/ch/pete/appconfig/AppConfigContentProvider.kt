package ch.pete.appconfig

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.preference.PreferenceManager
import ch.pete.appconfig.util.SignatureUtils


class AppConfigContentProvider : ContentProvider() {
    companion object {
        private const val TAG = "AppConfig"
    }

    override fun onCreate() = true

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?, selectionArgs: Array<out String>?
    ): Int {
        context?.let { context ->
            checkIfAllowed(context, values)

            var appliedKeysCount = 0
            values?.let { values ->
                AppConfig.appConfigListener?.let {
                    Handler(Looper.getMainLooper()).post {
                        it(values)
                    }
                }

                if (AppConfig.storeValuesToSharedPreferences) {
                    appliedKeysCount = storeValuesToSharedPreferences(context, values)
                }
            }

            return appliedKeysCount
        } ?: throw IllegalArgumentException("context is null")
    }

    private fun storeValuesToSharedPreferences(context: Context, values: ContentValues): Int {
        var appliedKeysCount = 0
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
        return appliedKeysCount
    }

    @Throws(SecurityException::class)
    private fun checkIfAllowed(context: Context, values: ContentValues?) {
        val callingApplicationId = context.packageManager.getNameForUid(Binder.getCallingUid())
        if (callingApplicationId == null) {
            Log.e(TAG, "callingApplicationId is null")
        }

        val allowedApp = callingApplicationId?.let {
            val callingSignature =
                SignatureUtils.getSignatureHash(context, callingApplicationId)
            Log.d(TAG, "callingApp: $callingApplicationId, callingAppSignature: $callingSignature")

            AppConfig.authorizedApps.firstOrNull {
                callingApplicationId == it.applicationId
                        && callingSignature == it.signature
            }
        }

        allowedApp ?: throw SecurityException("Access Denied")
        if (values?.containsKey("TEST_ACCESS_DENIED") == true) {
            throw SecurityException("Access Denied")
        } else {
            Log.d(TAG, "authorized access to $allowedApp")
        }
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
