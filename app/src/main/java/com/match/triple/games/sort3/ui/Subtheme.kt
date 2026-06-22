package com.match.triple.games.sort3.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.match.triple.games.sort3.ActivityViewModel
import com.match.triple.games.sort3.ui.FishHunterApp
import com.match.triple.games.sort3.ui.theme.FishHunterTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubTheme() {
    val viewModel: ActivityViewModel = koinViewModel()
    val showSplashScreen by viewModel.showSplash.collectAsState()
    val showMenuScreen by viewModel.showMenu.collectAsState()
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        val activity = context as? Activity ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(activity.window, view)
        if (showSplashScreen) {
            insetsController.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.statusBars())
            insetsController.isAppearanceLightStatusBars = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenOrientation(viewModel)

        if (showMenuScreen) {
            FishHunterTheme(dynamicColor = false) {
                FishHunterApp(modifier = Modifier.fillMaxSize())
            }
        }

        if (showSplashScreen) {
            SplashScreen()
        }
    }
}
