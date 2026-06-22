package com.match.triple.games.sort3

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.match.triple.games.sort3.ui.SubTheme
import com.match.triple.games.sort3.ui.FishHunterApp
import com.match.triple.games.sort3.ui.theme.FishHunterTheme

/**
 * Single activity that hosts the whole Compose UI. Navigation between the five game screens
 * is handled inside [FishHunterApp] via a NavHost; all persisted state lives in the shared
 * GameViewModel, so the activity itself stays thin.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            SubTheme()
        }
    }
}
