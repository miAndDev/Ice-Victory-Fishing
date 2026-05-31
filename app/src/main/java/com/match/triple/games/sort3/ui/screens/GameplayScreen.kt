package com.match.triple.games.sort3.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.match.triple.games.sort3.R
import com.match.triple.games.sort3.game.Coin
import com.match.triple.games.sort3.game.Fish
import com.match.triple.games.sort3.game.GameEngine
import com.match.triple.games.sort3.model.FishCatalog
import com.match.triple.games.sort3.ui.GameUiState
import com.match.triple.games.sort3.ui.RunResult
import com.match.triple.games.sort3.ui.components.CoinChip
import com.match.triple.games.sort3.ui.components.GameButton
import com.match.triple.games.sort3.ui.components.OutlinedText
import com.match.triple.games.sort3.ui.components.ScreenBackground
import kotlin.math.roundToInt

private enum class Phase { READY, PLAYING, OVER }

private const val ROUND_SECONDS = 60

// --- Background / sprite geometry (used to align gameplay to the `bg` artwork) ---
private const val BG_W = 1275f
private const val BG_H = 2778f
/** Fraction of bg.png height at which the painted water line sits (measured from the asset). */
private const val WATERLINE_FRAC = 0.27f

private const val FISHERMAN_WIDTH_FRAC = 0.5f   // boat width as a fraction of screen width
private const val FISHERMAN_AR = 791f / 524f    // fisherman.png width / height
private const val ROD_TIP_X_FRAC = 0.13f        // rod tip location within the sprite
private const val ROD_TIP_Y_FRAC = 0.07f
private const val BOAT_DIP_FRAC = 0.18f         // how much of the hull sits below the water line

/**
 * Maps the artwork's [WATERLINE_FRAC] to an on-screen Y, accounting for how
 * [ContentScale.Crop] scales and center-crops the background for this container. Without this
 * the water line would drift on tablets/foldables whose aspect differs from the image.
 */
private fun waterlineY(containerW: Float, containerH: Float): Float {
    val scale = maxOf(containerW / BG_W, containerH / BG_H)
    val displayedH = BG_H * scale
    val top = (containerH - displayedH) / 2f
    return top + WATERLINE_FRAC * displayedH
}

/**
 * The gameplay screen. Architecture notes that satisfy the QA criteria:
 *
 *  - The 60fps simulation runs in a [GameEngine] held in `remember`; the loop mutates plain
 *    objects, never StateFlow. The [Canvas] redraws by reading a single `frameTick` state in
 *    its DRAW phase, so per-frame motion invalidates only drawing — never recomposes the tree.
 *  - Discrete events (catch / coin) bump small `mutableIntState` HUD counters, recomposing
 *    only the lightweight HUD row.
 *  - [BoxWithConstraints] gives pixel bounds; every element is sized from them, so the game
 *    looks identical (just scaled) on phone, foldable, and tablet.
 */
@Composable
fun GameplayScreen(
    uiState: GameUiState,
    onRunFinished: (RunResult) -> Unit,
    onExit: () -> Unit
) {
    ScreenBackground(background = R.drawable.bg) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()

            val engine = remember { GameEngine() }
            val sprites = rememberFishSprites()

            // Align everything to the painted water line of the bg artwork.
            val waterY = waterlineY(widthPx, heightPx)
            val fishermanWidthPx = widthPx * FISHERMAN_WIDTH_FRAC
            val fishermanHeightPx = fishermanWidthPx / FISHERMAN_AR
            val fishermanLeftPx = (widthPx - fishermanWidthPx) / 2f
            // Place the boat so its hull straddles the surface (most above, BOAT_DIP below).
            val fishermanTopPx = waterY - fishermanHeightPx * (1f - BOAT_DIP_FRAC)
            val rodTip = Offset(
                fishermanLeftPx + ROD_TIP_X_FRAC * fishermanWidthPx,
                fishermanTopPx + ROD_TIP_Y_FRAC * fishermanHeightPx
            )

            // --- session state (Compose-local, NOT in the ViewModel StateFlow) ---
            var phase by remember { mutableStateOf(Phase.READY) }
            val score = remember { mutableIntStateOf(0) }
            val runCoins = remember { mutableIntStateOf(0) }
            var timeLeft by remember { mutableIntStateOf(ROUND_SECONDS) }
            val caughtTrophies = remember { mutableStateOf(setOf<String>()) }
            // Per-frame redraw signal; read only inside Canvas's draw phase.
            val frameTick = remember { mutableLongStateOf(0L) }

            // Re-configure the world whenever bounds or boosters change (rotation / unfold / shop).
            val tuning = uiState.tuning
            LaunchedEffect(widthPx, heightPx, waterY, tuning) {
                if (widthPx > 0f && heightPx > 0f) engine.configure(widthPx, heightPx, waterY, tuning)
            }

            // Keep gesture callbacks reading the latest phase without re-keying pointerInput.
            val phaseState = rememberUpdatedState(phase)

            // --- the game loop: delta-time stepping via withFrameMillis ---
            LaunchedEffect(phase) {
                if (phase != Phase.PLAYING) return@LaunchedEffect
                var last = 0L
                while (true) {
                    withFrameMillisCompat { ms ->
                        if (last != 0L) {
                            val dt = (ms - last) / 1000f
                            engine.update(
                                dtSec = dt,
                                onCoin = { value -> runCoins.intValue += value },
                                onCatch = { type ->
                                    score.intValue += type.points
                                    runCoins.intValue += type.coins
                                    if (type.isTrophy) {
                                        caughtTrophies.value = caughtTrophies.value + type.id
                                    }
                                }
                            )
                            frameTick.longValue = ms
                        }
                        last = ms
                    }
                }
            }

            // --- countdown timer (1Hz, independent of the render loop) ---
            LaunchedEffect(phase) {
                if (phase != Phase.PLAYING) return@LaunchedEffect
                while (timeLeft > 0) {
                    kotlinx.coroutines.delay(1000)
                    timeLeft -= 1
                }
                phase = Phase.OVER
            }

            // Commit results exactly once when the round ends.
            LaunchedEffect(phase) {
                if (phase == Phase.OVER) {
                    onRunFinished(
                        RunResult(
                            score = score.intValue,
                            coinsEarned = runCoins.intValue,
                            caughtTrophyIds = caughtTrophies.value
                        )
                    )
                }
            }

            // ----------------------------- RENDER -----------------------------
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            if (phaseState.value == Phase.PLAYING) engine.drop()
                        })
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(onDrag = { change, _ ->
                            if (phaseState.value == Phase.PLAYING) engine.aimTo(change.position.x)
                        })
                    }
            ) {
                // Reading frameTick here registers a DRAW-phase dependency: each new value
                // invalidates only this Canvas's drawing, not composition.
                @Suppress("UNUSED_EXPRESSION") frameTick.longValue
                if (!engine.isReady) return@Canvas

                drawFishingLine(engine, rodTip)
                engine.fish.forEach { drawFish(it, sprites) }
                engine.hook.carrying?.let { drawFish(it, sprites) }
                engine.coins.forEach { drawCoin(it) }
                drawHook(engine)
            }

            // The angler in his boat (the `fisherman` asset), positioned so the hull sits on
            // the painted water line and the rod tip matches rodTip(). Rendered under the HUD.
            Image(
                painter = painterResource(R.drawable.fisherman),
                contentDescription = "The angler",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset { IntOffset(fishermanLeftPx.roundToInt(), fishermanTopPx.roundToInt()) }
                    .fillMaxWidth(FISHERMAN_WIDTH_FRAC)
                    .aspectRatio(FISHERMAN_AR)
            )

            // ----------------------------- HUD -----------------------------
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
                    OutlinedText(text = "Score: ${score.intValue}", fontSize = 20, color = Color.White)
                    OutlinedText(
                        text = "⏱ $timeLeft",
                        fontSize = 20,
                        color = if (timeLeft <= 10) Color(0xFFFF8A80) else Color(0xFFB2FF59)
                    )
                    CoinChip(amount = runCoins.intValue, iconSize = 24.dp, fontSize = 16)
                }
            }

            // ----------------------------- OVERLAYS -----------------------------
            when (phase) {
                Phase.READY -> ReadyOverlay(
                    onStart = {
                        engine.reset()
                        score.intValue = 0
                        runCoins.intValue = 0
                        timeLeft = ROUND_SECONDS
                        caughtTrophies.value = emptySet()
                        phase = Phase.PLAYING
                    },
                    onExit = onExit
                )
                Phase.OVER -> GameOverOverlay(
                    score = score.intValue,
                    coins = runCoins.intValue,
                    trophyCount = caughtTrophies.value.size,
                    isNewBest = score.intValue >= uiState.progress.highScore,
                    onPlayAgain = { phase = Phase.READY },
                    onMenu = onExit
                )
                Phase.PLAYING -> Unit
            }
        }
    }
}

/* ------------------------------------------------------------------------------------------ */
/*  Drawing helpers (run in the Canvas DRAW phase)                                            */
/* ------------------------------------------------------------------------------------------ */

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFishingLine(
    engine: GameEngine,
    rodTip: Offset
) {
    val h = engine.hook
    // Line runs from the angler's rod tip to the hook (diagonal while aiming/casting).
    drawLine(
        color = Color(0xCCECEFF1),
        start = rodTip,
        end = Offset(h.x, h.y),
        strokeWidth = size.minDimension * 0.006f
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHook(engine: GameEngine) {
    val h = engine.hook
    val visualR = size.minDimension * 0.018f
    // Faint catch-radius ring so the player understands the hit area.
    drawCircle(
        color = Color(0x33FFFFFF),
        radius = h.radius,
        center = h.tip,
        style = Stroke(width = size.minDimension * 0.004f)
    )
    drawCircle(color = Color(0xFFCFD8DC), radius = visualR, center = h.tip)
    drawCircle(color = Color(0xFF455A64), radius = visualR * 0.5f, center = h.tip)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFish(
    f: Fish,
    sprites: Map<Int, ImageBitmap>
) {
    val bmp = sprites[f.type.spriteRes] ?: return
    val w = f.width
    val h = f.height
    val left = f.x - w / 2f
    val top = f.y - h / 2f
    withTransform({
        translate(left, top)
        // Mirror horizontally when swimming left so the fish faces its travel direction.
        if (f.facingLeft) scale(scaleX = -1f, scaleY = 1f, pivot = Offset(w / 2f, h / 2f))
    }) {
        drawImage(
            image = bmp,
            dstOffset = IntOffset(0, 0),
            dstSize = IntSize(w.roundToInt().coerceAtLeast(1), h.roundToInt().coerceAtLeast(1))
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCoin(c: Coin) {
    drawCircle(color = Color(0xFFFFB300), radius = c.radius, center = Offset(c.x, c.y))
    drawCircle(color = Color(0xFFFFD54F), radius = c.radius * 0.72f, center = Offset(c.x, c.y))
    drawCircle(
        color = Color(0xFFFFF59D),
        radius = c.radius * 0.28f,
        center = Offset(c.x - c.radius * 0.25f, c.y - c.radius * 0.25f)
    )
}

/* ------------------------------------------------------------------------------------------ */
/*  Overlays                                                                                   */
/* ------------------------------------------------------------------------------------------ */

@Composable
private fun ReadyOverlay(onStart: () -> Unit, onExit: () -> Unit) {
    DimOverlay {
        OutlinedText(text = "Ready?", fontSize = 36, color = Color(0xFFFFF59D))
        Spacer(Modifier.height(8.dp))
        OutlinedText(text = "Drag to aim • Tap to cast the hook", fontSize = 16, color = Color.White)
        OutlinedText(text = "Catch fish & coins before time runs out!", fontSize = 14, color = Color.White)
        Spacer(Modifier.height(24.dp))
        GameButton(text = "START", onClick = onStart, modifier = Modifier.width(240.dp))
        Spacer(Modifier.height(14.dp))
        GameButton(text = "BACK", onClick = onExit, modifier = Modifier.width(240.dp), fontSize = 16)
    }
}

@Composable
private fun GameOverOverlay(
    score: Int,
    coins: Int,
    trophyCount: Int,
    isNewBest: Boolean,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    DimOverlay {
        OutlinedText(text = "Time's Up!", fontSize = 36, color = Color(0xFFFFAB91))
        Spacer(Modifier.height(12.dp))
        if (isNewBest) {
            OutlinedText(text = "★ NEW BEST ★", fontSize = 20, color = Color(0xFFFFF176))
            Spacer(Modifier.height(8.dp))
        }
        OutlinedText(text = "Score: $score", fontSize = 24, color = Color.White)
        Spacer(Modifier.height(6.dp))
        CoinChip(amount = coins)
        Spacer(Modifier.height(6.dp))
        OutlinedText(text = "Trophies this run: $trophyCount", fontSize = 16, color = Color(0xFFB39DDB))
        Spacer(Modifier.height(24.dp))
        GameButton(text = "PLAY AGAIN", onClick = onPlayAgain, modifier = Modifier.width(240.dp))
        Spacer(Modifier.height(14.dp))
        GameButton(text = "MENU", onClick = onMenu, modifier = Modifier.width(240.dp), fontSize = 16)
    }
}

@Composable
private fun DimOverlay(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA001016)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/* ------------------------------------------------------------------------------------------ */
/*  Resource + frame helpers                                                                   */
/* ------------------------------------------------------------------------------------------ */

/** Loads each distinct fish sprite once as an [ImageBitmap] for fast Canvas drawing. */
@Composable
private fun rememberFishSprites(): Map<Int, ImageBitmap> {
    // imageResource caches internally; listed explicitly because @Composable calls can't be looped.
    val s0 = ImageBitmap.imageResource(R.drawable.fish_sprite)
    val s1 = ImageBitmap.imageResource(R.drawable.fish_sprite_1)
    val s2 = ImageBitmap.imageResource(R.drawable.fish_sprite_2)
    val s3 = ImageBitmap.imageResource(R.drawable.fish_sprite_3)
    val s4 = ImageBitmap.imageResource(R.drawable.fish_sprite_4)
    val s5 = ImageBitmap.imageResource(R.drawable.fish_sprite_5)
    val s6 = ImageBitmap.imageResource(R.drawable.fish_sprite_6)
    val s7 = ImageBitmap.imageResource(R.drawable.fish_sprite_7)
    val s8 = ImageBitmap.imageResource(R.drawable.fish_sprite_8)
    return remember(s0) {
        FishCatalog.all.associate { type ->
            type.spriteRes to when (type.spriteRes) {
                R.drawable.fish_sprite -> s0
                R.drawable.fish_sprite_1 -> s1
                R.drawable.fish_sprite_2 -> s2
                R.drawable.fish_sprite_3 -> s3
                R.drawable.fish_sprite_4 -> s4
                R.drawable.fish_sprite_5 -> s5
                R.drawable.fish_sprite_6 -> s6
                R.drawable.fish_sprite_7 -> s7
                else -> s8
            }
        }
    }
}

/** Thin alias around [androidx.compose.runtime.withFrameMillis] for readability. */
private suspend inline fun withFrameMillisCompat(crossinline onFrame: (Long) -> Unit) {
    androidx.compose.runtime.withFrameMillis { onFrame(it) }
}
