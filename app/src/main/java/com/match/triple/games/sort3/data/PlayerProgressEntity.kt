package com.match.triple.games.sort3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row persistence table holding the whole player profile.
 *
 * We keep one canonical row ([SINGLETON_ID]) and always upsert it, which makes the
 * "load once, observe forever" pattern trivial and avoids accidental duplicates.
 *
 * Booster ownership and trophies are stored as CSV blobs of their string keys. For a
 * small, fixed catalog this is far simpler than join tables and trivially serializable,
 * while still being a stable schema we can migrate later if the catalog grows.
 */
@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val highScore: Int = 0,
    val totalCoins: Int = 0,
    /** Booster level keyed by Booster.id, encoded as "id:level" pairs joined by ';'. */
    val boosterLevelsCsv: String = "",
    /** Set of unlocked trophy ids (rare fish caught), joined by ';'. */
    val trophiesCsv: String = ""
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
