package com.match.triple.games.sort3.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.match.triple.games.sort3.data.GameDatabase
import com.match.triple.games.sort3.data.PlayerProgressRepository
import com.match.triple.games.sort3.model.BoosterCatalog
import com.match.triple.games.sort3.model.PlayerProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Owns all persisted game state and the rules that mutate it. Exposes one [StateFlow] of
 * [GameUiState]. Every DB write goes through the repository on [kotlinx.coroutines.Dispatchers.IO];
 * the UI only ever reads the resulting state, satisfying the MVVM/MVI contract.
 *
 * AndroidViewModel is used so we can build the Room-backed repository from the app context
 * without a DI framework, while still surviving configuration changes.
 */
class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = PlayerProgressRepository(
        GameDatabase.get(app).playerProgressDao()
    )

    /** Transient messages live outside the DB; combined into the public state. */
    private val messages = MutableStateFlow<String?>(null)

    val uiState: StateFlow<GameUiState> =
        combine(repository.progress, messages) { progress, message ->
            GameUiState(isLoading = false, progress = progress, message = message)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GameUiState(isLoading = true)
        )

    init {
        viewModelScope.launch { repository.ensureInitialized() }
    }

    /**
     * Attempts to buy the next level of a booster. Validates affordability and level cap,
     * mutates progress, and persists — the change then flows back through [uiState], so the
     * Shop UI and the gameplay engine both see the new value on the next state emission.
     */
    fun buyBooster(boosterId: String) {
        val current = uiState.value.progress
        val booster = BoosterCatalog.byId(boosterId) ?: return
        val level = current.boosterLevel(boosterId)

        if (level >= booster.maxLevel) {
            messages.value = "${booster.displayName} is already maxed out"
            return
        }
        val cost = booster.costForLevel(level)
        if (current.totalCoins < cost) {
            messages.value = "Not enough coins — need $cost"
            return
        }

        val updated = current.copy(
            totalCoins = current.totalCoins - cost,
            boosterLevels = current.boosterLevels + (boosterId to level + 1)
        )
        messages.value = "${booster.displayName} upgraded to Lv.${level + 1}!"
        persist(updated)
    }

    /** Commits a finished round: bumps high score, banks coins, unlocks trophies. */
    fun commitRun(result: RunResult) {
        val current = uiState.value.progress
        val updated = current.copy(
            highScore = maxOf(current.highScore, result.score),
            totalCoins = current.totalCoins + result.coinsEarned,
            trophies = current.trophies + result.caughtTrophyIds
        )
        persist(updated)
    }

    fun consumeMessage() {
        messages.value = null
    }

    private fun persist(progress: PlayerProgress) {
        viewModelScope.launch { repository.save(progress) }
    }
}
