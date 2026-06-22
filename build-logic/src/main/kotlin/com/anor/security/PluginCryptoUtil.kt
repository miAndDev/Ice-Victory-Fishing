package com.anor.security

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object PluginCryptoUtil {
    private const val SECRET_KEY = "1234567890123456"
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    @Suppress("NewApi")
    fun encrypt(input: String): String {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }
}