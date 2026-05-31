package com.match.triple.games.sort3.model

/**
 * Converts owned booster levels into concrete gameplay multipliers. This is the single
 * place that maps "what the player bought" to "how the game behaves", so the Shop and the
 * gameplay engine can never disagree about an upgrade's effect.
 */
data class GameTuning(
    val hookSpeedMultiplier: Float,
    val hookRadiusMultiplier: Float,
    val luckMultiplier: Float
) {
    companion object {
        fun from(progress: PlayerProgress): GameTuning {
            fun mult(boosterId: String): Float {
                val booster = BoosterCatalog.byId(boosterId) ?: return 1f
                return 1f + booster.effectPerLevel * progress.boosterLevel(boosterId)
            }
            return GameTuning(
                hookSpeedMultiplier = mult(BoosterCatalog.REEL_SPEED),
                hookRadiusMultiplier = mult(BoosterCatalog.HOOK_SIZE),
                luckMultiplier = mult(BoosterCatalog.LUCK)
            )
        }
    }
}
