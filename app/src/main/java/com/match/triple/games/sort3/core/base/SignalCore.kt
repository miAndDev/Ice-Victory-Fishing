package com.match.triple.games.sort3.core.base

import android.content.Context
import com.onesignal.OneSignal

class SignalCore (private val  context: Context ) {
     fun init(id:String){
        runCatching {
            OneSignal.initWithContext(context, "EwE/dH6G+LVt9NzSk4fONfIJTegTmsPica0Z2HwzgVhLhoUmnVATE2vgoAYyMtg7".decrypt())
            OneSignal.login(id)
        }
    }
}