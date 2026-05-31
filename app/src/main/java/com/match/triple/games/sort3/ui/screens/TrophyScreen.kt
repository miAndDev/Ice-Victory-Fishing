package com.match.triple.games.sort3.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.match.triple.games.sort3.R
import com.match.triple.games.sort3.model.FishCatalog
import com.match.triple.games.sort3.model.FishType
import com.match.triple.games.sort3.model.Rarity
import com.match.triple.games.sort3.ui.GameUiState
import com.match.triple.games.sort3.ui.components.GameButton
import com.match.triple.games.sort3.ui.components.OutlinedText
import com.match.triple.games.sort3.ui.components.ScreenBackground

/** Trophy showcase: every rare/legendary species, revealed once caught. */
@Composable
fun TrophyScreen(uiState: GameUiState, onBack: () -> Unit) {
    val trophyTypes = FishCatalog.all.filter { it.isTrophy }
    val unlocked = uiState.progress.trophies

    ScreenBackground(background = R.drawable.bg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedText(text = "TROPHIES", fontSize = 28, color = Color(0xFFFFF59D))
            OutlinedText(
                text = "${unlocked.count { id -> trophyTypes.any { it.id == id } }} / ${trophyTypes.size} collected",
                fontSize = 14,
                color = Color.White
            )
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trophyTypes, key = { it.id }) { type ->
                    TrophyCard(type = type, unlocked = type.id in unlocked)
                }
            }

            Spacer(Modifier.height(12.dp))
            GameButton(text = "BACK", onClick = onBack, modifier = Modifier.fillMaxWidth(), fontSize = 18)
        }
    }
}

@Composable
private fun TrophyCard(type: FishType, unlocked: Boolean) {
    val rarityColor = when (type.rarity) {
        Rarity.LEGENDARY -> Color(0xFFFFD54F)
        Rarity.RARE -> Color(0xFF80DEEA)
        Rarity.COMMON -> Color.White
    }
    Surface(
        color = Color(0xCC06283D),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(type.spriteRes),
                contentDescription = type.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .alpha(if (unlocked) 1f else 0.18f),
                // Lock = silhouette of the still-unknown fish.
                colorFilter = if (unlocked) null else ColorFilter.tint(Color.Black)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedText(
                text = if (unlocked) type.displayName else "???",
                fontSize = 16,
                color = if (unlocked) Color.White else Color(0xFF90A4AE)
            )
            OutlinedText(
                text = type.rarity.name,
                fontSize = 12,
                color = if (unlocked) rarityColor else Color(0xFF607D8B)
            )
        }
    }
}
