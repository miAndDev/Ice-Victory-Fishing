package com.match.triple.games.sort3.data

import com.match.triple.games.sort3.model.PlayerProgress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Single source of truth for persisted progress. Owns the entity<->domain mapping and
 * guarantees every DB touch happens off the main thread via [io].
 *
 * The repository is intentionally "dumb": it persists whatever the ViewModel computed.
 * All game rules (costs, level caps, reward math) live in the ViewModel/model layer.
 */
class PlayerProgressRepository(
    private val dao: PlayerProgressDao,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {

    /** Cold stream of progress, re-emitted by Room on every write. Never null downstream. */
    val progress: Flow<PlayerProgress> =
        dao.observe().map { it?.toDomain() ?: PlayerProgress() }

    /** Ensures the singleton row exists so the very first launch has something to observe. */
    suspend fun ensureInitialized() = withContext(io) {
        if (dao.getOnce() == null) {
            dao.upsert(PlayerProgressEntity())
        }
    }

    suspend fun save(progress: PlayerProgress) = withContext(io) {
        dao.upsert(progress.toEntity())
    }

    // --- mapping helpers -----------------------------------------------------------

    private fun PlayerProgressEntity.toDomain(): PlayerProgress = PlayerProgress(
        highScore = highScore,
        totalCoins = totalCoins,
        boosterLevels = decodeBoosters(boosterLevelsCsv),
        trophies = decodeSet(trophiesCsv)
    )

    private fun PlayerProgress.toEntity(): PlayerProgressEntity = PlayerProgressEntity(
        highScore = highScore,
        totalCoins = totalCoins,
        boosterLevelsCsv = encodeBoosters(boosterLevels),
        trophiesCsv = encodeSet(trophies)
    )

    private fun decodeBoosters(csv: String): Map<String, Int> =
        csv.split(';')
            .filter { it.isNotBlank() }
            .mapNotNull { pair ->
                val (id, lvl) = pair.split(':').let { it.getOrNull(0) to it.getOrNull(1) }
                val level = lvl?.toIntOrNull()
                if (id.isNullOrBlank() || level == null) null else id to level
            }
            .toMap()

    private fun encodeBoosters(map: Map<String, Int>): String =
        map.entries.joinToString(";") { "${it.key}:${it.value}" }

    private fun decodeSet(csv: String): Set<String> =
        csv.split(';').filter { it.isNotBlank() }.toSet()

    private fun encodeSet(set: Set<String>): String = set.joinToString(";")
}
