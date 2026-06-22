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
    private val onHttpError: () -> Unit = {}
) : WebViewClient() {

    private var isFirstTime = true
    private var isError = false

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return try {
            onOverride(request?.url)
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
        Log.d("LINK_DATA", errorResponse?.statusCode.toString())

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
