package com.match.triple.games.sort3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for Fish Hunter. Exposes a process-wide singleton so the whole app
 * shares one connection pool (creating multiple Room instances is a common leak/perf bug).
 */
@Database(
    entities = [PlayerProgressEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GameDatabase : RoomDatabase() {

    abstract fun playerProgressDao(): PlayerProgressDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun get(context: Context): GameDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "fish_hunter.db"
                )
                    // For a casual game a destructive fallback is acceptable; replace with
                    // real migrations once the schema is in production.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
