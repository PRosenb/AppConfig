package ch.pete.appconfig.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and


internal object SignatureUtils {
    @Throws(PackageManager.NameNotFoundException::class, NoSuchAlgorithmException::class)
    fun getSignatureHash(context: Context, packageName: String?): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val sig: Signature = context.packageManager
            .getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures[0]
        return toHexStringWithColons(
            md.digest(sig.toByteArray())
        )
    }

    // based on https://stackoverflow.com/a/2197650/115145
    private fun toHexStringWithColons(bytes: ByteArray): String {
        val hexArray = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
        )
        val hexChars = CharArray(bytes.size * 3 - 1)
        var v: Int
        for (j in bytes.indices) {
            v = (bytes[j] and 0xF).toInt()
            hexChars[j * 3] = hexArray[v / 16]
            hexChars[j * 3 + 1] = hexArray[v % 16]
            if (j < bytes.size - 1) {
                hexChars[j * 3 + 2] = ':'
            }
        }
        return String(hexChars)
    }
}
