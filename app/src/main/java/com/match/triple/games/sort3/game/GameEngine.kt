package com.match.triple.games.sort3.game

import com.match.triple.games.sort3.model.FishCatalog
import com.match.triple.games.sort3.model.FishType
import com.match.triple.games.sort3.model.GameTuning
import com.match.triple.games.sort3.model.Rarity
import kotlin.random.Random

/**
 * Headless, frame-stepped game simulation. It holds the mutable world and advances it by a
 * delta-time each frame. It performs NO drawing and touches NO Compose state — the gameplay
 * composable reads [fish]/[coins]/[hook] directly inside its Canvas draw phase and forwards
 * discrete events (catch / coin) to the ViewModel.
 *
 * Everything is sized as a fraction of the play-field bounds, so the simulation looks and
 * feels identical on a phone, foldable, or tablet (just scaled).
 */
class GameEngine {

    // --- world ---------------------------------------------------------------------
    val fish = ArrayList<Fish>(24)
    val coins = ArrayList<Coin>(16)
    lateinit var hook: Hook
        private set

    var width = 0f
        private set
    var height = 0f
        private set
    var surfaceY = 0f
        private set
    private var bottomY = 0f

    // --- derived, bounds-relative tuning -------------------------------------------
    private var fishBaseWidth = 0f
    private var dropSpeed = 0f
    private var reelSpeed = 0f
    private var maxFish = 8
    private var tuning = GameTuning(1f, 1f, 1f)

    private var fishSpawnAcc = 0f
    private var coinSpawnAcc = 0f
    private val rng = Random.Default

    private var configured = false
    val isReady: Boolean get() = configured

    /**
     * (Re)sizes the world and applies the player's boosters. Safe to call again when the
     * window size changes (foldable unfold / rotation) — positions are clamped back in-bounds.
     */
    /**
     * @param surfaceYpx the on-screen Y of the artwork's water line (computed by the screen
     *        from the background's Crop mapping) — fish, coins and the idle hook all live below it.
     */
    fun configure(widthPx: Float, heightPx: Float, surfaceYpx: Float, tuning: GameTuning) {
        width = widthPx
        height = heightPx
        this.tuning = tuning
        // Clamp so there is always a usable play area below the surface.
        surfaceY = surfaceYpx.coerceIn(heightPx * 0.08f, heightPx * 0.55f)
        bottomY = heightPx * 0.95f

        val unit = minOf(widthPx, heightPx)
        fishBaseWidth = widthPx * 0.14f
        dropSpeed = heightPx * 0.85f
        reelSpeed = heightPx * 0.55f * tuning.hookSpeedMultiplier
        maxFish = (widthPx / (fishBaseWidth * 1.4f)).toInt().coerceIn(5, 12)

        if (!configured) {
            hook = Hook(x = widthPx / 2f, y = surfaceY, surfaceY = surfaceY).also {
                it.targetX = widthPx / 2f
            }
        }
        // Catch radius scales with the screen and the "Bigger Hook" booster.
        hook.radius = unit * 0.045f * tuning.hookRadiusMultiplier
        hook.x = hook.x.coerceIn(0f, widthPx)
        configured = true
    }

    fun reset() {
        fish.clear()
        coins.clear()
        fishSpawnAcc = 0f
        coinSpawnAcc = 0f
        if (configured) {
            hook.state = HookState.IDLE
            hook.carrying = null
            hook.x = width / 2f
            hook.y = surfaceY
            hook.targetX = width / 2f
        }
    }

    // --- input ---------------------------------------------------------------------

    /** Aim the hook horizontally while it's idle at the surface. */
    fun aimTo(xPx: Float) {
        hook.targetX = xPx.coerceIn(0f, width)
    }

    /** Cast the line. Ignored unless the hook is idle. */
    fun drop() {
        if (configured && hook.state == HookState.IDLE) {
            hook.state = HookState.DROPPING
        }
    }

    // --- simulation ----------------------------------------------------------------

    /**
     * Advances the world by [dtSec] seconds. [onCoin] fires when a coin is collected,
     * [onCatch] when a reeled-in fish reaches the surface.
     */
    fun update(dtSec: Float, onCoin: (Int) -> Unit, onCatch: (FishType) -> Unit) {
        if (!configured) return
        val dt = dtSec.coerceAtMost(0.05f) // clamp huge frame gaps (e.g. after a stall)

        spawn(dt)
        stepFish(dt)
        stepCoins(dt)
        stepHook(dt, onCoin, onCatch)
    }

    private fun spawn(dt: Float) {
        fishSpawnAcc += dt
        if (fishSpawnAcc >= FISH_SPAWN_INTERVAL && fish.size < maxFish) {
            fishSpawnAcc = 0f
            spawnFish()
        }
        coinSpawnAcc += dt
        if (coinSpawnAcc >= COIN_SPAWN_INTERVAL && coins.size < MAX_COINS) {
            coinSpawnAcc = 0f
            spawnCoin()
        }
    }

    private fun spawnFish() {
        val type = pickWeightedType()
        val w = fishBaseWidth * type.sizeFactor
        val fromLeft = rng.nextBoolean()
        val speed = width * 0.16f * type.speedFactor
        val laneTop = surfaceY + w
        val laneBottom = bottomY - w
        val y = if (laneBottom > laneTop) rng.nextFloat() * (laneBottom - laneTop) + laneTop
        else (surfaceY + bottomY) / 2f
        val x = if (fromLeft) -w else width + w
        val vx = if (fromLeft) speed else -speed
        fish.add(Fish(type = type, x = x, y = y, vx = vx, width = w))
    }

    private fun spawnCoin() {
        val r = width * 0.032f
        val x = rng.nextFloat() * (width - 2 * r) + r
        val y = bottomY - rng.nextFloat() * (bottomY - surfaceY) * 0.5f
        val drift = -(height * 0.05f) // gentle upward drift
        // Lucky Lure makes coins worth a little more on average.
        val value = (1 + rng.nextInt(3) + (tuning.luckMultiplier - 1f).toInt())
        coins.add(Coin(x = x, y = y, vy = drift, radius = r, value = value.coerceAtLeast(1)))
    }

    /** Weighted random species pick; rare/legendary weights scale with the Luck booster. */
    private fun pickWeightedType(): FishType {
        val luck = tuning.luckMultiplier
        var total = 0f
        for (t in FishCatalog.all) total += weightOf(t, luck)
        var roll = rng.nextFloat() * total
        for (t in FishCatalog.all) {
            roll -= weightOf(t, luck)
            if (roll <= 0f) return t
        }
        return FishCatalog.all.first()
    }

    private fun weightOf(t: FishType, luck: Float): Float =
        if (t.rarity == Rarity.COMMON) t.spawnWeight else t.spawnWeight * luck

    private fun stepFish(dt: Float) {
        val it = fish.iterator()
        while (it.hasNext()) {
            val f = it.next()
            if (f.caught) { it.remove(); continue }
            f.x += f.vx * dt
            val pad = f.width
            if (f.x < -pad || f.x > width + pad) it.remove()
        }
    }

    private fun stepCoins(dt: Float) {
        val it = coins.iterator()
        while (it.hasNext()) {
            val c = it.next()
            if (c.collected) { it.remove(); continue }
            c.ageSeconds += dt
            c.y += c.vy * dt
            // gentle horizontal bob using age (no per-frame random)
            c.x += kotlin.math.sin(c.ageSeconds * 3f) * (width * 0.0008f)
            if (c.y < surfaceY - c.radius || c.ageSeconds > COIN_LIFETIME) it.remove()
        }
    }

    private fun stepHook(dt: Float, onCoin: (Int) -> Unit, onCatch: (FishType) -> Unit) {
        val h = hook
        when (h.state) {
            HookState.IDLE -> {
                // Ease horizontally toward the aim point for a smooth, framerate-independent glide.
                val ease = 1f - Math.pow(0.0001, dt.toDouble()).toFloat()
                h.x += (h.targetX - h.x) * ease
                h.y = surfaceY
            }
            HookState.DROPPING -> {
                h.y += dropSpeed * dt
                collectCoins(onCoin)
                if (h.carrying == null) tryHookFish()
                if (h.y >= bottomY || h.carrying != null) h.state = HookState.REELING
            }
            HookState.REELING -> {
                h.y -= reelSpeed * dt
                collectCoins(onCoin)
                h.carrying?.let { caught ->
                    caught.x = h.x
                    caught.y = h.y + caught.height * 0.4f
                }
                if (h.y <= surfaceY) {
                    h.y = surfaceY
                    h.carrying?.let { onCatch(it.type) }
                    h.carrying = null
                    h.state = HookState.IDLE
                }
            }
        }
    }

    private fun tryHookFish() {
        val h = hook
        for (f in fish) {
            if (f.caught) continue
            if (circleIntersectsRect(
                    cx = h.x, cy = h.y, r = h.radius,
                    rx = f.x - f.width / 2f, ry = f.y - f.height / 2f,
                    rw = f.width, rh = f.height
                )
            ) {
                f.caught = true
                h.carrying = f
                return
            }
        }
    }

    private fun collectCoins(onCoin: (Int) -> Unit) {
        val h = hook
        for (c in coins) {
            if (c.collected) continue
            if (circleIntersectsCircle(h.x, h.y, h.radius, c.x, c.y, c.radius)) {
                c.collected = true
                onCoin(c.value)
            }
        }
    }

    companion object {
        private const val FISH_SPAWN_INTERVAL = 0.75f
        private const val COIN_SPAWN_INTERVAL = 2.0f
        private const val COIN_LIFETIME = 9f
        private const val MAX_COINS = 6

        /** Circle (cx,cy,r) vs axis-aligned rect (rx,ry,rw,rh). */
        fun circleIntersectsRect(
            cx: Float, cy: Float, r: Float,
            rx: Float, ry: Float, rw: Float, rh: Float
        ): Boolean {
            val closestX = cx.coerceIn(rx, rx + rw)
            val closestY = cy.coerceIn(ry, ry + rh)
            val dx = cx - closestX
            val dy = cy - closestY
            return dx * dx + dy * dy <= r * r
        }

        fun circleIntersectsCircle(
            ax: Float, ay: Float, ar: Float,
            bx: Float, by: Float, br: Float
        ): Boolean {
            val dx = ax - bx
            val dy = ay - by
            val rr = ar + br
            return dx * dx + dy * dy <= rr * rr
        }
    }
}
