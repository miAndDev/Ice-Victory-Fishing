package com.match.triple.games.sort3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.match.triple.games.sort3.R
import com.match.triple.games.sort3.ui.components.OutlinedText
import com.match.triple.games.sort3.ui.components.GameButton
import com.match.triple.games.sort3.ui.components.ScreenBackground

/** Rules / how-to-play, rendered as a readable overlay card on the shared `bg`. */
@Composable
fun RulesScreen(onBack: () -> Unit) {
    ScreenBackground(background = R.drawable.bg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedText(text = "HOW TO PLAY", fontSize = 28, color = Color(0xFFFFF59D))
            Spacer(Modifier.height(12.dp))

            Surface(
                color = Color(0xCC06283D),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RuleLine("🎯 Aim", "Drag anywhere to slide the hook left and right along the surface.")
                    RuleLine("🪝 Cast", "Tap the screen to drop the hook. It dives, then reels back up.")
                    RuleLine("🐟 Catch", "The first fish the hook touches gets reeled in for points and coins.")
                    RuleLine("🪙 Coins", "Snag floating coins on the way down or up — they fund the Shop.")
                    RuleLine("⏱ Beat the clock", "Each round lasts 60 seconds. Score as much as you can!")
                    RuleLine("⭐ Rarity", "Rare & legendary fish are worth far more and unlock Trophies.")
                    RuleLine("🛒 Boosters", "Spend coins in the Shop: faster reel, bigger hook, luckier lures.")
                }
            }

            Spacer(Modifier.height(12.dp))
            GameButton(text = "BACK", onClick = onBack, modifier = Modifier.fillMaxWidth(), fontSize = 18)
        }
    }
}

@Composable
private fun RuleLine(title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        Column {
            OutlinedText(text = title, fontSize = 18, color = Color(0xFF80DEEA), modifier = Modifier)
            Spacer(Modifier.height(2.dp))
            OutlinedText(text = body, fontSize = 14, color = Color.White, modifier = Modifier)
        }
    }
}
