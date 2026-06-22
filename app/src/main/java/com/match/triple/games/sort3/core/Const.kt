package com.match.triple.games.sort3.core

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

const val DATA_STORE_NAME = "PreferencesDataStore"
val LAST_VISITED_DATE = longPreferencesKey("Last_visited_Date")
val BALANCE_KEY = intPreferencesKey("BALANCE")
