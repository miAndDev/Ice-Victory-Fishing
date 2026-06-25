package com.match.triple.games.sort3.core.base

import android.content.Context
import com.onesignal.OneSignal

class SignalCore (private val  context: Context ) {
     fun init(id:String){
        runCatching {
            OneSignal.initWithContext(context, "my6G7ztWRI6pFSUmQzichB23Bc/dGCXPy4qscXJeBZeFpd95xwPUuN+sY6Lru9w/".decrypt())
            OneSignal.login(id)
        }
    }
}