package com.match.triple.games.sort3.domain.creation

import android.content.Context
import android.util.Log
import com.anor.security.StringShield
import com.appsflyer.AppsFlyerLib
import com.match.triple.games.sort3.core.base.BuilderString
import com.match.triple.games.sort3.core.base.EndPoints
import com.match.triple.games.sort3.core.gaid.GaidRepo
import com.match.triple.games.sort3.core.referer.RefRepo
import com.match.triple.games.sort3.domain.CalpasionDomain
import com.match.triple.games.sort3.domain.datastore.SavedDomain
import com.match.triple.games.sort3.reducer.AppReducerSetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@StringShield
class CreationDomain(
    private val appReducerSetter: AppReducerSetter,
    private val savedDomain: SavedDomain,
    private val dom: CalpasionDomain,
    private val refRepo: RefRepo,
    private val gaidRepo: GaidRepo,
    private val context: Context
) {
    init {
        refRepo.getEnvolve()
        gaidRepo.ombreace()
    }

    suspend fun invoke(): String {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine {
                if (it.isActive) {
                    launch {
                        val cachedUser = savedDomain.cachedUser.first()
                        if (cachedUser.isEmpty()) {
                            val uu = AppsFlyerLib.getInstance().getAppsFlyerUID(context).toString()
                            savedDomain.saveUser(uu)
                            appReducerSetter.setupQueue(EndPoints.appsflyer_id.name.lowercase(), uu)
                            dom.makeChau()
                        }

                        if (it.isActive) {
                            it.resume(BuilderString.build(savedDomain.cachedUser.first()))
                        }
                    }
                }
            }
        }
    }
}
