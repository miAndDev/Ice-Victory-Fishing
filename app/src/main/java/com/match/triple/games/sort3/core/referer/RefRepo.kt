package com.match.triple.games.sort3.core.referer


import com.match.triple.games.sort3.core.base.EndPoints
import com.match.triple.games.sort3.core.base.OrgRef
import com.match.triple.games.sort3.core.base.decrypt
import com.match.triple.games.sort3.core.dataStore.DataStoreRepo
import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import com.anor.security.StringShield
import com.match.triple.games.sort3.core.ref.InstallReferrerClient
import com.match.triple.games.sort3.core.ref.InstallReferrerStateListener
import com.match.triple.games.sort3.reducer.AppReducerSetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

@StringShield
class RefRepo(
    private val context: Context,
    private val appReducerSetter: AppReducerSetter,
) {

    init {
        getRef()
    }

    fun getEnvolve(): String {
        return "Get evaluate"
    }

    private fun getRef() {
        val client = runCatching {
            InstallReferrerClient.newBuilder(context).build()
        }.getOrNull() ?: return

        runCatching {
            val listener = object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    if (p0 == 0) {
                        runCatching {
                            val ref = client.installReferrer.installReferrer.toString()

                                appReducerSetter.setupQueue(
                                    EndPoints.refferer.name.lowercase(),
                                    runCatching {
                                        URLEncoder.encode(ref, "DYMfwGKTaeIC38mAKKFN1g==".decrypt())
                                    }.getOrNull() ?: ""
                                )

                        }
                    }
                    runCatching { client.endConnection() }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    runCatching { client.endConnection() }
                }
            }
            client.startConnection(listener)
        }
    }
}
