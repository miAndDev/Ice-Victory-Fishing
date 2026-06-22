package com.match.triple.games.sort3.core.dataStore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepo {

    suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T): T
    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)
    fun <T> getDataByKey(key: Preferences.Key<T>, defValue: T): Flow<T>
}