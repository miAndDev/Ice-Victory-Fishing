package com.match.triple.games.sort3.reducer

import kotlinx.coroutines.flow.StateFlow

interface AppReducerState {
    val appColposion: StateFlow<Map<String, String>>
}