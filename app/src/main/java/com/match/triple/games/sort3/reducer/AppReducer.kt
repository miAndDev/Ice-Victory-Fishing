package com.match.triple.games.sort3.reducer

import androidx.datastore.preferences.core.intPreferencesKey
import com.match.triple.games.sort3.core.dataStore.DataStoreRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AppReducer(private val dataStoreRepo: DataStoreRepo) : AppReducerState, AppReducerSetter,
    AppReducerPosition {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _appState = MutableStateFlow<Map<String, String>>(emptyMap())

    private val _appPosition: StateFlow<Int> =
        dataStoreRepo.getDataByKey(intPreferencesKey("app_colppos"), 0)
            .stateIn(scope, SharingStarted.Eagerly, 0)

    override fun setupQueue(key: String, value: String) {
        _appState.update { it + (key to value) }
    }

    override val appPosition: StateFlow<Int> get() = _appPosition

    override val appColposion: StateFlow<Map<String, String>> get() = _appState.asStateFlow()
}
