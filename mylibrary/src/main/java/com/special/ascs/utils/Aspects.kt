package com.special.ascs.utils

import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class   Aspects(
    private val onOverride: (Uri?) -> Boolean,
    private val clearHistory: (String) -> Unit,
    private val onCheck: (String) -> Unit,
    private val onHttpError: () -> Unit = {},
    private val onExternalIntentLaunched: (() -> Unit)? = null
) : WebViewClient() {

    private var isFirstTime = true
    private var isError = false

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return try {
            val handled = onOverride(request?.url)
            // The URL was taken over by an external app, so this WebView will never
            // load a page. Notify so an empty popup window can be torn down.
            if (handled) onExternalIntentLaunched?.invoke()
            handled
        } catch (_: Exception) {
            true
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)

        if (isFirstTime && request?.isForMainFrame == true && errorResponse?.statusCode == 403) {
            onHttpError()
            isError = true
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        isFirstTime = false

        if (isError) {
            isError = false
            return
        }

        val str = view?.title
        str?.let {
            clearHistory(it)
        }
        url?.let {
            onCheck(it)
        }
    }
}
