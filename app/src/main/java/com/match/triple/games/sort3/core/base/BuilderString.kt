package com.match.triple.games.sort3.core.base

import com.anor.security.StringShield
import com.match.triple.games.sort3.core.AESUtil
import com.match.triple.games.sort3.core.PostRequest
import java.net.URLEncoder
@StringShield
object BuilderString {
    fun build(ref: String, user: String, gaid: String): PostRequest {
        return PostRequest(
            url = ClassForName().toString(),
            mapOf("key1" to AESUtil.encrypt(provideInfo(ref, user, gaid)))
        )
    }

    fun build(user: String): String {
        return ClassForName().toString() + "/?"+ EndPoints.appsflyer_id.name.lowercase() +"="+user
    }

    private fun provideInfo(
        ref: String,
        user: String,
        gaid: String
    ): String {
        return "${EndPoints.appsflyer_id.name.lowercase()}=$user&${EndPoints.adv_id.name.lowercase()}=$gaid&${EndPoints.referrer.name.lowercase()}=${
            runCatching {
                URLEncoder.encode(ref, "DYMfwGKTaeIC38mAKKFN1g==".decrypt())
            }.getOrNull()
        }"

    }


}