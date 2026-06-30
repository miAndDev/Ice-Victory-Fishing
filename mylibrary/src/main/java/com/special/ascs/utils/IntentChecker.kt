package com.special.ascs.utils

import com.anor.security.StringShield
import android.content.Context
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.content.Intent.parseUri
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri
import com.special.ascs.decrypt

@StringShield
class IntentChecker {
    fun getIntentForUrl(url: String): Intent = when {

        url.startsWith("46wW0vj2PuM4S+Wg29mkeQ==".decrypt()) -> Intent("zUDudTPsmfPfZERyHMBIk4+lxZrtKUa+Jo+IBg0r0gI=".decrypt()).apply {
            type = "2UYRTTetASKjf4tkfBdW7Q==".decrypt()
            putExtra("q7Gynyarz3dV1HSQdEsf2QYEEpVNicsQC4rgpD5PRXU=".decrypt(), arrayOf(url.removePrefix("46wW0vj2PuM4S+Wg29mkeQ==".decrypt())))
        }

        url.startsWith("CK5KSZpI55lE9O5vxAplIx0aeyujKkUQhiH58sI8haI=".decrypt()) -> Intent("zUDudTPsmfPfZERyHMBIk09/HPjln0Ts8mNkrYGQ044=".decrypt(), url.toUri())
        url.startsWith("k/sEsN9GMjmZM29AodsCcA==".decrypt()) -> Intent("zUDudTPsmfPfZERyHMBIk44U633MbnbEnFR2Pa4W980=".decrypt(), url.toUri())
        url.contains("Kcd1xDW70EwPdJznHPecnQ==".decrypt()) || url.contains("xTdAwS6D4KdN5H3M9u9+5A==".decrypt()) -> parseUri(url, URI_INTENT_SCHEME)

        else -> Intent("zUDudTPsmfPfZERyHMBIk09/HPjln0Ts8mNkrYGQ044=".decrypt(), url.toUri())
    }

    fun getChooserTitle(url: String): String = when {
        url.startsWith("46wW0vj2PuM4S+Wg29mkeQ==".decrypt()) -> "7/+npmCJspHtOQ8N6Yj1ww==".decrypt()
        url.startsWith("k/sEsN9GMjmZM29AodsCcA==".decrypt()) -> "SPaUcaWoeSeLA2SLlN9stg==".decrypt()
        else -> "nnjObRcfw+VfmmvBMcts7A==".decrypt()
    }

    // Launch the best intent for [url]; on failure, route the user to the store and
    // finally surface a message. [fallbackPackageName] (e.g. resolved from app rules)
    // wins over any package the intent itself carries.
    fun launchUrl(context: Context, url: String, fallbackPackageName: String? = null): Boolean {
        val intent = getIntentForUrl(url).withLaunchFlags()
        if (tryStart(context, intent)) return true

        val packageName = fallbackPackageName
            ?: intent.`package`
            ?: intent.component?.packageName
            ?: extractPackageName(intent.data)

        if (packageName != null && openInStore(packageName, context)) return true

        Toast.makeText(context, "No application found", Toast.LENGTH_SHORT).show()
        return false
    }

    // Open [url] directly in [packageName]; if that app is missing, fall back to its store page.
    fun launchUrlInPackage(context: Context, url: String, packageName: String): Boolean {
        val intent = getIntentForUrl(url).apply {
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (tryStart(context, intent)) return true
        if (openInStore(packageName, context)) return true

        Toast.makeText(context, "No application found", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun Intent.withLaunchFlags(): Intent = apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Force a real app (not an in-app browser) to handle plain web links on API 30+.
        if (Build.VERSION.SDK_INT >= 30 &&
            action == "android.intent.action.VIEW" &&
            data?.scheme in listOf("http", "https")
        ) {
            addFlags(Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER)
        }
    }

    private fun extractPackageName(uri: Uri?): String? =
        uri?.getQueryParameter("id")?.takeIf { it.isNotBlank() }

    fun openInStore(packageName: String, context: Context?): Boolean {
        context ?: return false
        val view = "zUDudTPsmfPfZERyHMBIk09/HPjln0Ts8mNkrYGQ044=".decrypt()
        val marketUri = "${"aGMa7WutDJmot9CnGLK3+Y0QYdincKcC3krA79PJWcU=".decrypt()}$packageName".toUri()
        val webUri = "${"KFr/g/oEAgT82s2a8PS1pdWVVCGcPaCfEED9sViiPSsnKYdV2ctXPDAuPPFbZnL/\n".decrypt()}$packageName".toUri()

        // 1) Play Store app explicitly, 2) any market handler, 3) Play Store website.
        val vendingMarket = Intent(view, marketUri)
            .apply { setPackage("com.android.vending"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        val genericMarket = Intent(view, marketUri)
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        val web = Intent(view, webUri)
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        return tryStart(context, vendingMarket) ||
            tryStart(context, genericMarket) ||
            tryStart(context, web)
    }

    fun tryStart(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }
}