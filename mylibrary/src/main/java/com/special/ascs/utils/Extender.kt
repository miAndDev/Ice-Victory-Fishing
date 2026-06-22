package com.special.ascs.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import com.special.ascs.utils.Rulz.APP_RULES

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

    private fun handleUrl(url: String, context: Context?): Boolean =
        when {
            url.startsWith("about:", ignoreCase = true) -> false
            url.startsWith("blob:", ignoreCase = true) -> false
            url.startsWith("data:", ignoreCase = true) -> false
            extHelper.isHttpsAccordingToRules(url) -> false
            else -> {
                launchIntentForUrl(url, context)
                true
            }
        }

    private fun launchIntentForUrl(url: String, context: Context?) {
        context ?: return
        runCatching {
            val intent = checker.getIntentForUrl(url)
                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

            // Try to open the target app/intent directly first.
            if (checker.tryStart(context, intent)) return@runCatching

            // App is missing — resolve its package, then send the user to the store.
            val packageName = intent.`package`
                ?: intent.component?.packageName
                ?: APP_RULES.entries.firstOrNull { url.contains(it.key, ignoreCase = true) }?.value

            packageName?.let { checker.openInStore(it, context) }
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
        Log.d("LINK_DATA", "Chau finished $str")
        onHideSplash()
    }

    fun checkOnceMore(str: String, default: String) {
        if (!str.contains(default)) {
            onSaveData(str)
        }
    }

}