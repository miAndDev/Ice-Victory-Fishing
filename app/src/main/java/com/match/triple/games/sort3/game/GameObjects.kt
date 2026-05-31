package com.match.triple.games.sort3.game

import androidx.compose.ui.geometry.Offset
import com.match.triple.games.sort3.model.FishType

/** Vertical state machine for the fishing hook. */
enum class HookState { IDLE, DROPPING, REELING }

/**
 * Live, mutable fish instance on screen. Mutated in place every frame by the engine to
 * avoid per-frame allocations (important for a smooth 60fps loop with many fish).
 */
class Fish(
    val type: FishType,
    var x: Float,
    var y: Float,
    /** Horizontal velocity in px/sec. Sign encodes swim direction. */
    var vx: Float,
    /** Drawn width in px; height is derived from the sprite aspect at draw time. */
    var width: Float
) {
    val height: Float get() = width * SPRITE_ASPECT
    var caught: Boolean = false

    /** Faces left when swimming left so the sprite can be mirrored. */
    val facingLeft: Boolean get() = vx < 0f

    companion object {
        /** Fish sprites are roughly 2:1 (wider than tall). */
        const val SPRITE_ASPECT = 0.6f
    }
}

/** Collectible coin that bobs and drifts upward, then expires. */
class Coin(
    var x: Float,
    var y: Float,
    var vy: Float,
    val radius: Float,
    val value: Int
) {
    var collected: Boolean = false
    var ageSeconds: Float = 0f
}

/**
 * The hook + line. [x]/[y] is the hook tip. [targetX] is where the player is aiming while
 * the hook is idle at the surface; the tip eases toward it for a smooth feel.
 */
class Hook(
    var x: Float,
    var y: Float,
    val surfaceY: Float
) {
    var targetX: Float = x
    var state: HookState = HookState.IDLE
    var radius: Float = 0f
    /** The fish currently hooked and being reeled up, if any. */
    var carrying: Fish? = null

    val tip: Offset get() = Offset(x, y)
}
