package com.match.triple.games.sort3.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.match.triple.games.sort3.R
import com.match.triple.games.sort3.ui.GameUiState
import com.match.triple.games.sort3.ui.components.CoinChip
import com.match.triple.games.sort3.ui.components.GameButton
import com.match.triple.games.sort3.ui.components.OutlinedText
import com.match.triple.games.sort3.ui.components.ScreenBackground

/** Main menu. Uses the dedicated `menu_bg` artwork and stylized [GameButton]s. */
@Composable
fun MenuScreen(
    uiState: GameUiState,
    onPlay: () -> Unit,
    onShop: () -> Unit,
    onTrophies: () -> Unit,
    onRules: () -> Unit
) {
    ScreenBackground(background = R.drawable.menu_bg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: coins + best score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinChip(amount = uiState.progress.totalCoins)
                OutlinedText(
                    text = "Best: ${uiState.progress.highScore}",
                    fontSize = 16,
                    color = Color(0xFF80DEEA)
                )
            }

            Spacer(Modifier.height(24.dp))
            OutlinedText(text = "FISH HUNTER", fontSize = 42, color = Color(0xFFFFF59D))
            OutlinedText(text = "Cast • Catch • Collect", fontSize = 16, color = Color.White)

            Spacer(Modifier.weight(1f))

            // Hero angler (the `fisherman` asset, formerly pers.png).
            Image(
                painter = painterResource(R.drawable.fisherman),
                contentDescription = "The angler",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .aspectRatio(791f / 524f)
            )

            Spacer(Modifier.weight(1f))

            val buttonWidth = Modifier.width(260.dp)
            GameButton(text = "PLAY", onClick = onPlay, modifier = buttonWidth)
            Spacer(Modifier.height(16.dp))
            GameButton(text = "SHOP", onClick = onShop, modifier = buttonWidth)
            Spacer(Modifier.height(16.dp))
            GameButton(text = "TROPHIES", onClick = onTrophies, modifier = buttonWidth)
            Spacer(Modifier.height(16.dp))
            GameButton(text = "RULES", onClick = onRules, modifier = buttonWidth)

            Spacer(Modifier.weight(1f))
        }
    }
}
