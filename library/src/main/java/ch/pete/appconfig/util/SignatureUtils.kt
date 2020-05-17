package ch.pete.appconfig.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest
import kotlin.experimental.and

internal object SignatureUtils {
    /**
     * For Android before P the current signature is fetched only.
     */
    fun getCurrentAndPastSignatures(context: Context, packageName: String): List<String> {
        val signatures: Array<Signature> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo =
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                ).signingInfo
            if (signingInfo.hasMultipleSigners()) {
                throw SecurityException("Permission denied. Apps signed with multiple signatures are not supported.")
            } else {
                signingInfo.signingCertificateHistory
            }
        } else {
            @Suppress("DEPRECATION")
            @SuppressLint("PackageManagerGetSignatures")
            val signatures = context.packageManager
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            if (signatures.size > 1) {
                throw SecurityException("Permission denied. Apps signed with multiple signatures are not supported.")
            }
            arrayOf(signatures[0])
        }

        val sha256MessageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
        return signatures.map {
            val sha256Bytes = sha256MessageDigest.digest(it.toByteArray())
            toHexStringWithColons(sha256Bytes)
        }
    }

    // based on https://stackoverflow.com/a/2197650/115145
    @Suppress("MagicNumber")
    private fun toHexStringWithColons(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 3 - 1)
        for (index in bytes.indices) {
            val v = (bytes[index] and 0xF).toInt()
            hexChars[index * 3] = hexArray[v / 16]
            hexChars[index * 3 + 1] = hexArray[v % 16]
            if (index < bytes.size - 1) {
                hexChars[index * 3 + 2] = ':'
            }
        }
        return String(hexChars)
    }
}
