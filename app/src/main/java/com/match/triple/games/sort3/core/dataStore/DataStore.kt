package com.match.triple.games.sort3.core.dataStore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.match.triple.games.sort3.core.DATA_STORE_NAME

val Context.dataStore by preferencesDataStore(
    name = DATA_STORE_NAME
)