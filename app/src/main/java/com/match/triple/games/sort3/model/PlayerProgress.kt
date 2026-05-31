package com.match.triple.games.sort3.model

/**
 * Domain representation of saved progress — the parsed, ready-to-use form of
 * [com.match.triple.games.sort3.data.PlayerProgressEntity]. Composables and the
 * ViewModel work with this, never with the raw CSV strings.
 */
data class PlayerProgress(
    val highScore: Int = 0,
    val totalCoins: Int = 0,
    val boosterLevels: Map<String, Int> = emptyMap(),
    val trophies: Set<String> = emptySet()
) {
    fun boosterLevel(boosterId: String): Int = boosterLevels[boosterId] ?: 0
}
