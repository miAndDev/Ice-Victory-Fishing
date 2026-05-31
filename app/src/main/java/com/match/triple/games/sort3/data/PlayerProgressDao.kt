package com.match.triple.games.sort3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data-access for the single player-progress row.
 *
 * [observe] returns a cold [Flow] that Room re-emits on every write, so the ViewModel
 * never has to poll — the UI state stays in lock-step with the database.
 */
@Dao
interface PlayerProgressDao {

    @Query("SELECT * FROM player_progress WHERE id = :id LIMIT 1")
    fun observe(id: Int = PlayerProgressEntity.SINGLETON_ID): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = :id LIMIT 1")
    suspend fun getOnce(id: Int = PlayerProgressEntity.SINGLETON_ID): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PlayerProgressEntity)
}
