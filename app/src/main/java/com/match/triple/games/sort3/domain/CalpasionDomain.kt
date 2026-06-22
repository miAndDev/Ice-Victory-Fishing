package com.match.triple.games.sort3.domain

import android.util.Log
import com.anor.security.StringShield
import com.match.triple.games.sort3.core.Kch
import com.match.triple.games.sort3.core.PostRequest
import com.match.triple.games.sort3.core.base.ClassForName
import com.match.triple.games.sort3.reducer.AppReducer
import kotlinx.coroutines.delay

@StringShield
class CalpasionDomain(private val appReducer: AppReducer) {
    suspend fun makeChau() {
        var map = appReducer.appColposion.value
        while (map.keys.size < 3) {
            delay(100)
            map = appReducer.appColposion.value
        }
        Log.d("LINK_DATA", "chau entered ${map.keys}")
        Kch().kchau(PostRequest(ClassForName().toString(), map))
    }
}
