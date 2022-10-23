package ch.pete.appconfig.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest

internal object SignatureUtils {
    private const val HEX_FF = 0xff

    /**
     * For Android before P the current signature is fetched only.
     */
    fun getCurrentAndPastSignatures(context: Context, packageName: String): List<String> {
        val signatures: Array<Signature> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(
                        packageName,
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong())
                    ).signingInfo
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    ).signingInfo
                }
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

    private fun toHexStringWithColons(hash: ByteArray): String {
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(HEX_FF and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex.uppercase())
            hexString.append(":")
        }
        if (hexString.isNotEmpty()) {
            // remove last colon
            hexString.setLength(hexString.length - 1)
        }
        return hexString.toString()
    }
}
