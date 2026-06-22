package com.match.triple.games.sort3.domain

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.match.triple.games.sort3.core.dataStore.DataStoreRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID


class SavedDataDomain (private val dataStoreRepo: DataStoreRepo) {


    private val cachedUserKey = stringPreferencesKey("cached-user-key")
    private val isUserAllowedToPlayKey = booleanPreferencesKey("is-user-allowed-to-play-key")
    private val savedInfoKey = stringPreferencesKey("saved-info-key")


    val getSavedInfo = dataStoreRepo.getDataByKey(savedInfoKey, "").flowOn(Dispatchers.IO)
    val getIsUserAllowedToPlay = dataStoreRepo.getDataByKey(isUserAllowedToPlayKey, false).flowOn(
        Dispatchers.IO
    )
    val getCachedUser = dataStoreRepo.getDataByKey(cachedUserKey, "").map {
        it.ifEmpty {
            val uid = UUID.randomUUID().toString()
            dataStoreRepo.putPreference(cachedUserKey, uid)
            uid
        }
    }


    suspend fun saveInfo(newInfo: String) {
        withContext(Dispatchers.IO) {
            if (getSavedInfo.first().isEmpty()) {
                dataStoreRepo.putPreference(savedInfoKey, newInfo)
            }
        }
    }


    suspend fun allowUserToEnter() {
        withContext(Dispatchers.IO) {
            dataStoreRepo.putPreference(isUserAllowedToPlayKey, true)
        }
    }

}