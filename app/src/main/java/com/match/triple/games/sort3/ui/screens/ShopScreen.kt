package com.match.triple.games.sort3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.match.triple.games.sort3.R
import com.match.triple.games.sort3.model.Booster
import com.match.triple.games.sort3.model.BoosterCatalog
import com.match.triple.games.sort3.ui.GameUiState
import com.match.triple.games.sort3.ui.components.CoinChip
import com.match.triple.games.sort3.ui.components.GameButton
import com.match.triple.games.sort3.ui.components.OutlinedText
import com.match.triple.games.sort3.ui.components.ScreenBackground

/** Booster store. Buying instantly updates the ViewModel state, persists, and (because the
 *  gameplay engine reads the same [GameUiState.tuning]) changes how the next round plays. */
@Composable
fun ShopScreen(
    uiState: GameUiState,
    onBuy: (String) -> Unit,
    onConsumeMessage: () -> Unit,
    onBack: () -> Unit
) {
    // Auto-dismiss transient purchase messages.
    LaunchedEffect(uiState.message) {
        if (uiState.message != null) {
            kotlinx.coroutines.delay(1800)
            onConsumeMessage()
        }
    }

    ScreenBackground(background = R.drawable.bg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedText(text = "SHOP", fontSize = 28, color = Color(0xFFFFF59D))
                CoinChip(amount = uiState.progress.totalCoins)
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(BoosterCatalog.all, key = { it.id }) { booster ->
                    val level = uiState.progress.boosterLevel(booster.id)
                    BoosterCard(
                        booster = booster,
                        level = level,
                        coins = uiState.progress.totalCoins,
                        onBuy = { onBuy(booster.id) }
                    )
                }
            }

            uiState.message?.let {
                OutlinedText(
                    text = it,
                    fontSize = 16,
                    color = Color(0xFFFFF176),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            GameButton(
                text = "BACK",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18
            )
        }
    }
}

@Composable
private fun BoosterCard(
    booster: Booster,
    level: Int,
    coins: Int,
    onBuy: () -> Unit
) {
    val maxed = level >= booster.maxLevel
    val cost = booster.costForLevel(level)
    val affordable = coins >= cost

    Surface(
        color = Color(0xCC06283D),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedText(
                    text = booster.displayName,
                    fontSize = 20,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(2.dp))
                OutlinedText(
                    text = booster.description,
                    fontSize = 13,
                    color = Color(0xFFB0BEC5),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(6.dp))
                LevelDots(level = level, max = booster.maxLevel)
            }

            Spacer(Modifier.width(12.dp))

            Box(contentAlignment = Alignment.Center) {
                if (maxed) {
                    OutlinedText(text = "MAX", fontSize = 18, color = Color(0xFF80CBC4))
                } else {
                    GameButton(
                        text = "$cost",
                        onClick = onBuy,
                        enabled = affordable,
                        minWidth = 96.dp,
                        minHeight = 52.dp,
                        fontSize = 18
                    )
                }
            }
        }
    }
}

/** Visual level meter: filled vs empty pips. */
@Composable
private fun LevelDots(level: Int, max: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(max) { i ->
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .then(
                        Modifier.background(
                            if (i < level) Color(0xFF4CAF50) else Color(0x44FFFFFF)
                        )
                    )
            )
        }
    }
}
