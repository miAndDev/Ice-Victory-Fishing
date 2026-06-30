package com.special.ascs.utils

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import com.anor.security.StringShield
import com.special.ascs.utils.Rulz.APP_RULES
import com.special.ascs.utils.Rulz.DIRECT_APP_RULES
@StringShield
class Extender(
    private val onHideSplash: () -> Unit,
    private val onShowMenu: () -> Unit,
    private val onSaveData: (String) -> Unit
) {
    private val checker by lazy { IntentChecker() }
    private val extHelper by lazy { ExtHelper() }
    fun check(str: Uri?, context: WebView?): Boolean {
        return handleUrl(str.toString(), context?.context)
    }

    private fun handleUrl(url: String, context: Context?): Boolean {
        if (url.startsWith("about:", ignoreCase = true) ||
            url.startsWith("blob:", ignoreCase = true) ||
            url.startsWith("data:", ignoreCase = true)
        ) return false

        // Payment/banking hosts are handed off to their own app directly, even when
        // the URL is plain https (which would otherwise stay in the WebView).
        val directPackage = DIRECT_APP_RULES.entries
            .firstOrNull { url.contains(it.key, ignoreCase = true) }
            ?.value
        if (directPackage != null) {
            context?.let { checker.launchUrlInPackage(it, url, directPackage) }
            return true
        }

        if (extHelper.isHttpsAccordingToRules(url)) return false

        launchIntentForUrl(url, context)
        return true
    }

    private fun launchIntentForUrl(url: String, context: Context?) {
        context ?: return
        runCatching {
            // Prefer an app-rule match for the store fallback, then defer the whole
            // launch/fallback flow (flags, store, messaging) to the checker.
            val fallbackPackageName = APP_RULES.entries
                .firstOrNull { url.contains(it.key, ignoreCase = true) }
                ?.value

            checker.launchUrl(context, url, fallbackPackageName)
        }
    }

    fun onError() {
        onShowMenu()
        onHideSplash()
    }

    fun checkAnother(str: String, header: String) {
        if (str.contains(
                header,
                true
            ) && header.isNotEmpty()
        ) {
            onShowMenu()
        }
        onHideSplash()
    }

    fun checkOnceMore(str: String, default: String) {
        if (!str.contains(default)) {
            onSaveData(str)
        }
    }

}