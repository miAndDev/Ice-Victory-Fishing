package com.match.triple.games.sort3.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Parcel
import com.anor.security.StringShield
import kotlinx.coroutines.suspendCancellableCoroutine

@StringShield
class AmbitClient {

    private val GMS_PACKAGE = "com.google.android.gms"
    private val GMS_ACTION = "com.google.android.gms.ads.identifier.service.START"
    private val DESCRIPTOR = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService"

    suspend fun getAdvertisingId(context: Context): String? = suspendCancellableCoroutine { continuation ->
        val intent = Intent(GMS_ACTION).apply {
            setPackage(GMS_PACKAGE)
        }

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service == null) {
                    continuation.resume(null, null)
                    runCatching { context.unbindService(this) }
                    return
                }

                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    service.transact(1, data, reply, 0)
                    reply.readException()
                    val adId = reply.readString()
                    continuation.resume(adId, null)
                } catch (e: Exception) {
                    continuation.resume(null, null)
                } finally {
                    reply.recycle()
                    data.recycle()
                    runCatching { context.unbindService(this) }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        val isBound = runCatching {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }.getOrDefault(false)

        if (!isBound) {
            continuation.resume(null, null)
        }
    }
}
