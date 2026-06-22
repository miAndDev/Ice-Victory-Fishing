package com.match.triple.games.sort3.core.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreRepoImpl(context: Context) : DataStoreRepo {
    private val dataSource = context.dataStore


    override suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T): T {
        return dataSource.data.first()[key] ?: defaultValue
    }

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataSource.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun <T> getDataByKey(
        key: Preferences.Key<T>,
        defValue: T
    ): Flow<T> {
       return dataSource.data.map{it[key] ?: defValue}
    }

}