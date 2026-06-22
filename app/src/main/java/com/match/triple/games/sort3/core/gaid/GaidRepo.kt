package com.match.triple.games.sort3.core.gaid

import android.content.Context
import com.anor.security.StringShield
import com.match.triple.games.sort3.core.AmbitClient
import com.match.triple.games.sort3.core.base.EndPoints
import com.match.triple.games.sort3.reducer.AppReducerSetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@StringShield
class GaidRepo(private val context: Context, private val appReducerSetter: AppReducerSetter) {
    init {
        getDefGaid()
    }

    private fun getDefGaid() {
        CoroutineScope(Dispatchers.IO).launch {
            val imberce = AmbitClient().getAdvertisingId(context)
            appReducerSetter.setupQueue(EndPoints.adv_id.name.lowercase(), imberce ?: "")
        }
    }

    fun ombreace(){

    }
}
