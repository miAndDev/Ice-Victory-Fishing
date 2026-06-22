package com.special.ascs

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anor.security.StringShield
import com.github.dhaval2404.imagepicker.ImagePicker
import com.special.ascs.utils.PolicyState
import com.special.ascs.utils.PolicyView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Keep
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposablePolicyView(
    initial: Flow<String>,
    onHideSpalsh: () -> Unit,
    onShowMenu: () -> Unit,
    checkHeader: String,
    defaultSaver: String,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    val viewModel: PolicyScreenViewModel = viewModel()
    var view by remember {
        mutableStateOf<WebView?>(null)
    }
    var buttonsEnabled by remember {
        mutableStateOf(true)
    }

    var filepathCallBack by remember {
        mutableStateOf<ValueCallback<Array<out Uri?>?>?>(null)
    }
    var permissionRequest by remember {
        mutableStateOf<PermissionRequest?>(null)
    }
    var progress by remember {
        mutableFloatStateOf(0f)
    }

    val showInetErro by viewModel.isInternetReachable.collectAsState()

    val scope = rememberCoroutineScope()

    val cameraPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            try {
                permissionRequest?.let { rs ->
                    if (it) {
                        rs.grant(rs.resources)
                    } else {
                        rs.deny()
                    }
                    permissionRequest = null
                }
            } catch (_: Exception) {

            }
        }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == -1) {
                val uri: Uri? = it.data?.data
                try {
                    filepathCallBack?.onReceiveValue(arrayOf(uri))
                    filepathCallBack = null
                } catch (e: Exception) {
                    filepathCallBack = null
                }
            }
            buttonsEnabled = true
        }


    val remmemberLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    var firstEnter by remember { mutableStateOf(false) }
    var shouldAsk by remember { mutableStateOf(false) }

    LaunchedEffect(shouldAsk) {
        if (shouldAsk && firstEnter) {
            if (Build.VERSION.SDK_INT >= 33 && activity?.checkSelfPermission("android.permission.POST_NOTIFICATIONS") != 0) {
                remmemberLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }
        firstEnter = true
    }

    LaunchedEffect(Unit) {
        viewModel.openHandler.collect {
            runCatching {
                    when (it) {
                        Actions.OpenC -> {
                            ImagePicker.with(context as Activity).cameraOnly().createIntent { intent ->
                                imagePicker.launch(intent)
                            }
                        }

                        Actions.OpenG -> {
                            ImagePicker.with(context as Activity).galleryOnly().createIntent {intent->
                                imagePicker.launch(intent)
                            }
                        }
                    }

            }.onFailure {

            }
        }
    }
    LaunchedEffect(permissionRequest) {
        permissionRequest?.let {
            cameraPermission.launch(viewModel.perm)
        }
    }



    LaunchedEffect(Unit) {
        view = PolicyView(
            context = context,
            onShowFileChooser = { filepathCallBack = it },
            onPermission = { permissionRequest = it },
            extender = viewModel.extender,
            header = checkHeader,
            default = defaultSaver,
            onNewProgress = {
                progress = it / 100f
            })
    }

    LaunchedEffect(Unit) {
        viewModel.navState.collect {
            when (it) {
                is PolicyState.OnHideSplash -> onHideSpalsh()
                is PolicyState.OnShowMenu -> onShowMenu()
                is PolicyState.OnSave -> {
                    shouldAsk = true
                    onSave(it.data)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        initial.collect {
            view?.loadUrl(it)
        }
    }

    BackHandler {
        if (view?.canGoBack() == true) {
            view?.goBack()
        }

    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        view?.let { webView ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(), factory = { context ->
                    webView
                })
        }



        if (progress in (0.01f..0.99f)) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            )
        }

        if (!showInetErro) {
            Text(
                text = "Looks like your internet connection is slow or corrupted",
                style = MaterialTheme.typography.bodyMedium.copy(
                    Color.White.copy(0.3f),
                    shadow = Shadow(color = Color.Black, blurRadius = 1f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )
        }
    }

    filepathCallBack?.let {
        Dialog(onDismissRequest = {
            try {
                filepathCallBack?.onReceiveValue(null)
                filepathCallBack = null
            } catch (_: Exception) {
                filepathCallBack = null
            }
        }) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, RoundedCornerShape(10.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Image(
                    Icons.Filled.Camera, modifier = Modifier
                        .weight(3f)
                        .aspectRatio(1f)
                        .clickable(enabled = buttonsEnabled) {
                            scope.launch {
                            buttonsEnabled = false
                                delay(50)
                            viewModel.openWithResult(Actions.OpenC)
                            }
                        }, contentDescription = "take", contentScale = ContentScale.FillBounds
                )
                Spacer(Modifier.weight(1f))
                Image(
                    Icons.Filled.ImageSearch,
                    modifier = Modifier
                        .weight(3f)
                        .aspectRatio(1f)
                        .clickable(enabled = buttonsEnabled) {
                            scope.launch {
                            buttonsEnabled = false
                                delay(50)
                            viewModel.openWithResult(Actions.OpenG)
                            }
                        },
                    contentDescription = "take",
                    contentScale = ContentScale.FillBounds
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }

}