package com.match.triple.games.sort3.domain.datastore

import com.match.triple.games.sort3.core.dataStore.DataStoreRepo
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SavedDomain (private val prefsRepoImpl: DataStoreRepo) {
    private val cachedUserKey = stringPreferencesKey("user-key")
    private val userSavedKey = booleanPreferencesKey("user-saved")
    private val userLoginKey = stringPreferencesKey("user-losgdgin-key")
    private val bosd = intPreferencesKey("bonuss")



    val userSaved = prefsRepoImpl.getDataByKey(userSavedKey, false).flowOn(Dispatchers.IO)
    val login = prefsRepoImpl.getDataByKey(userLoginKey,"").flowOn(Dispatchers.IO)
    val bonus = prefsRepoImpl.getDataByKey(bosd,0).flowOn(Dispatchers.IO)
    val cachedUser = prefsRepoImpl.getDataByKey(cachedUserKey, "")

    suspend fun saveUser(userKey: String){
        withContext(Dispatchers.IO) {
            prefsRepoImpl.putPreference(cachedUserKey, userKey)
        }
    }
    suspend fun saveUser(){
        withContext(Dispatchers.IO) {
            prefsRepoImpl.putPreference(userSavedKey, true)
        }
    }

    suspend fun saveLogin(login: String){
        withContext(Dispatchers.IO) {
            if (this@SavedDomain.login.first().isEmpty()) {
                prefsRepoImpl.putPreference(userLoginKey, login)
            }
        }
    }
    suspend fun saveBonus(login: Int){
        withContext(Dispatchers.IO) {
            prefsRepoImpl.putPreference(bosd, login)
        }
    }


}