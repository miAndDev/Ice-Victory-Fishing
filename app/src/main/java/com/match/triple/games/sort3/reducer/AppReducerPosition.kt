package com.match.triple.games.sort3.reducer

import kotlinx.coroutines.flow.StateFlow

interface AppReducerPosition {
    val appPosition: StateFlow<Int>
}