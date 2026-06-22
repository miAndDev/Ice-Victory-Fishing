package com.match.triple.games.sort3.core.base

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.preferencesDataStore
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun String.decrypt():String{
    val key = SecretKeySpec("2560CCB43E248CF5".toByteArray(), "AES")
    val iv = IvParameterSpec(ByteArray(16))
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, key, iv)
    val plainText = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
    return String(plainText)
}



val Context.dataStore by preferencesDataStore(
    name = "kbvfdhjkfdnk"
)