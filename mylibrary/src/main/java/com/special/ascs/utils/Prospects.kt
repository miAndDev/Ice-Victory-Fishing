package com.special.ascs.utils

import android.net.Uri
import android.os.Message
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.anor.security.StringShield
import com.special.ascs.decrypt

@StringShield
class Prospects(
    private val onRequestPermission: (PermissionRequest?) -> Unit,
    private val onShowFileChooser: (ValueCallback<Array<out Uri?>?>?) -> Unit,
    private val onCreateWindow: ((Message) -> Unit)? = null,
    private val onCloseWindow: ((WebView?) -> Unit)? = null,
    private val onNewProgress: (Int) -> Unit
) : WebChromeClient() {
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        onShowFileChooser(filePathCallback)
        return true
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onNewProgress(newProgress)
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        val createPopup = onCreateWindow ?: return false
        val popupMessage = resultMsg ?: return false
        createPopup(popupMessage)
        return true
    }

    override fun onCloseWindow(window: WebView?) {
        onCloseWindow?.invoke(window) ?: super.onCloseWindow(window)
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        when (request?.resources?.any { it == "Gq60NVXpy0oTJQwBDaqMPkYrUgPv7sokXb0zCIXaF2kKPRaOJuwKjMgG6tuwu0ok".decrypt() }) {
            true -> {
                onRequestPermission(request)
            }

            else -> {
                super.onPermissionRequest(request)
            }
        }
    }
}