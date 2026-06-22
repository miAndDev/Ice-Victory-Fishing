package com.match.triple.games.sort3

import android.app.Application
import com.anor.security.StringShield
import com.appsflyer.AppsFlyerLib
import com.match.triple.games.sort3.di.appModule
import com.match.triple.games.sort3.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@StringShield
class GameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@GameApplication)
            modules(appModule, domainModule)
        }
        AppsFlyerLib.getInstance().init("jAvZCoygo69rthjUr3jsJg", null, this)
        AppsFlyerLib.getInstance().setDebugLog(false)
        AppsFlyerLib.getInstance().start(this, "jAvZCoygo69rthjUr3jsJg")
    }
}
