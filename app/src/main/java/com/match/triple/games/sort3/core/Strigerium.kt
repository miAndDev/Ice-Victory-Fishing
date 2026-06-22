package com.match.triple.games.sort3.core

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object Strigerium {

    fun getAidlDescriptorInstallReferrer() =
        decrypt(EncryptedStrings.AIDL_DESCRIPTOR_INSTALL_REFERRER)

    fun getServicePackageVending() = decrypt(EncryptedStrings.SERVICE_PACKAGE_VENDING)
    fun getServiceNameInstallReferrer() = decrypt(EncryptedStrings.SERVICE_NAME_INSTALL_REFERRER)
    fun getServiceActionBindInstallReferrer() =
        decrypt(EncryptedStrings.SERVICE_ACTION_BIND_INSTALL_REFERRER)

    fun getPackageName() = decrypt(EncryptedStrings.PACKAGE)
    fun getReferrerKeyInstall() = decrypt(EncryptedStrings.REFERRER_KEY_INSTALL)
    fun getReferrerKeyClickTimestamp() = decrypt(EncryptedStrings.REFERRER_KEY_CLICK_TIMESTAMP)
    fun getReferrerKeyInstallBeginTimestamp() =
        decrypt(EncryptedStrings.REFERRER_KEY_INSTALL_BEGIN_TIMESTAMP)

}

internal fun decrypt(encrypted: String): String {
    return decryptAESECB(encrypted, aesKey)
}

private fun decryptAESECB(encryptedBase64: String, key: String): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    val keyBytes = key.toByteArray().copyOf(32)
    val secretKey = SecretKeySpec(keyBytes, "AES")

    cipher.init(Cipher.DECRYPT_MODE, secretKey)

    val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
    val decryptedBytes = cipher.doFinal(encryptedBytes)
    return String(decryptedBytes)
}


private val aesKey by lazy {
    decryptAESGCM()
}

private fun decryptAESGCM(): String {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val keyBytes = masterKey.toByteArray().copyOf(32)
    val secretKey = SecretKeySpec(keyBytes, "AES")

    val gcmSpec = GCMParameterSpec(128, iv)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

    val encryptedBytes = Base64.decode("itVwbnxzMK0v4Q7CYPAKQi3ztzEoCMRyFM1GeYnwYw5jSrF2TIo65GkMr1WX3RT9jrBGTCE7i/ia7fzjCgkgrA==", Base64.DEFAULT)
    val decryptedBytes = cipher.doFinal(encryptedBytes)
    return String(decryptedBytes)
}

private val masterKey = "dcf371d7d5c2c927f35741fea6d93e2ea8c16db6cda7dc65e5c5e7499dd39f21"
private val iv = Base64.decode("tN6TPYuSHZrOiA5S", Base64.DEFAULT)
