package com.match.triple.games.sort3.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.match.triple.games.sort3.R

/**
 * Full-bleed screen background. Uses [ContentScale.Crop] so the artwork fills any aspect
 * ratio (phone / foldable / tablet) without distortion — satisfying the visual-consistency
 * requirement. Children are laid out on top.
 */
@Composable
fun ScreenBackground(
    @DrawableRes background: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        content()
    }
}

/** Crisp, readable text over busy artwork via a faux outline (four offset copies). */
@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 20,
    color: Color = Color.White,
    outline: Color = Color(0xFF0B3A53),
    fontWeight: FontWeight = FontWeight.Black,
    textAlign: TextAlign = TextAlign.Center
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val base = TextStyle(fontSize = fontSize.sp, fontWeight = fontWeight, textAlign = textAlign)
        listOf(-2 to 0, 2 to 0, 0 to -2, 0 to 2).forEach { (dx, dy) ->
            Text(
                text = text,
                style = base.copy(color = outline),
                modifier = Modifier.offset { IntOffset(dx, dy) }
            )
        }
        Text(text = text, style = base.copy(color = color))
    }
}

/**
 * Themed button rendered on top of the `button` artwork. The image is the visual; the
 * clickable [Box] over it provides the interaction (default ripple via LocalIndication).
 * Disabled state dims the artwork and blocks clicks.
 */
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: Int = 20,
    minWidth: Dp = 220.dp,
    minHeight: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.button),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .alpha(if (enabled) 1f else 0.45f),
            contentScale = ContentScale.FillBounds
        )
        OutlinedText(
            text = text,
            fontSize = fontSize,
            color = if (enabled) Color.White else Color(0xFFB0BEC5),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

/** Small coin pill: the `ic_coin` icon next to an amount. Reused by HUD, Shop, Menu. */
@Composable
fun CoinChip(
    amount: Int,
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    fontSize: Int = 18
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ic_coin),
            contentDescription = "Coins",
            modifier = Modifier.size(iconSize)
        )
        Spacer(Modifier.width(6.dp))
        OutlinedText(text = "$amount", fontSize = fontSize, color = Color(0xFFFFE082))
    }
}
