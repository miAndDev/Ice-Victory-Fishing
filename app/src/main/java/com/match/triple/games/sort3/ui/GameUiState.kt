package com.match.triple.games.sort3.ui

import com.match.triple.games.sort3.model.GameTuning
import com.match.triple.games.sort3.model.PlayerProgress

/**
 * The single immutable UI state surfaced to every screen via [GameViewModel.uiState].
 *
 * It contains only *persisted / cross-screen* data. The fast-moving, per-frame gameplay
 * world (fish/hook positions, session score, countdown) is intentionally NOT here — it
 * lives in the gameplay engine and Compose-local state so the 60fps loop never pushes
 * through this StateFlow and never causes global recomposition.
 */
data class GameUiState(
    val isLoading: Boolean = true,
    val progress: PlayerProgress = PlayerProgress(),
    /** Transient one-shot message (e.g. "Not enough coins"), consumed by the UI. */
    val message: String? = null
) {
    val tuning: GameTuning get() = GameTuning.from(progress)
}

/** Outcome of a single fishing round, handed back to the ViewModel to persist. */
data class RunResult(
    val score: Int,
    val coinsEarned: Int,
    val caughtTrophyIds: Set<String>
)
