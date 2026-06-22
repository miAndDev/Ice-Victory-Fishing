package com.special.ascs.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView

@SuppressLint("ViewConstructor")
class PolicyView(
    private val header: String,
    private val default: String,
    private val context: Context,
    private val onShowFileChooser: (ValueCallback<Array<out Uri?>?>?) -> Unit,
    private val onPermission: (PermissionRequest?) -> Unit,
    private val extender: Extender, private val onNewProgress: (Int) -> Unit,
    private val onCloseWindow: ((WebView?) -> Unit)? = null
) : WebView(context) {
    init {
        initSettings()
        options()
        provideWebChrome()
        provideWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSettings() {
        settings.apply {
            javaScriptEnabled = true
            mixedContentMode = 0
            domStorageEnabled = true
            loadsImagesAutomatically = true
            databaseEnabled = true
            useWideViewPort = true
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            allowContentAccess = true
            setSupportMultipleWindows(true)
            builtInZoomControls = true
            setGeolocationEnabled(false)
            if (Build.VERSION.SDK_INT >= 26) {
                safeBrowsingEnabled = true
            }
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
            @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT <= 27) saveFormData =
                true

            val sanitizedAgent = userAgentString.replace(Regex("(; wv|Version/\\S+\\s)"), "")
            userAgentString = sanitizedAgent
        }
    }

    private fun options() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            importantForAutofill = IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        isSaveEnabled = true
        isFocusable = true
        isFocusableInTouchMode = true
        layoutParams = LayoutParams(
            -1,
            -1
        )
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private fun provideWebView() {
        webViewClient = Aspects(onOverride = {
                extender.check(it, context = this)
        }, clearHistory = {
            extender.checkAnother(it, header)
        }, onCheck = {
            extender.checkOnceMore(it, default)
        }, onHttpError = {
            extender.onError()
        })
    }


    private fun provideWebChrome() {
        webChromeClient = Prospects(onShowFileChooser = {
            onShowFileChooser(it)
        }, onRequestPermission = {
            onPermission(it)
        }, onCreateWindow = { resultMsg ->
            showPopup(resultMsg)
        }, onCloseWindow = onCloseWindow, onNewProgress = { onNewProgress(it) })
    }

    private fun showPopup(resultMsg: Message) {
        val dialog = Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar)
        val popupView = PolicyView(
            header = header,
            default = default,
            context = context,
            onShowFileChooser = onShowFileChooser,
            onPermission = onPermission,
            extender = extender,
            onNewProgress = onNewProgress,
            onCloseWindow = { window ->
                dialog.dismiss()
                window?.destroy()
            }
        )
        dialog.setContentView(popupView)
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog.setOnDismissListener { popupView.destroy() }
        dialog.show()

        val transport = resultMsg.obj as? WebView.WebViewTransport ?: return
        transport.webView = popupView
        resultMsg.sendToTarget()
    }


}