package com.match.triple.games.sort3.core.base

class AdbClassName {
    private val adbEnabled by lazy{
        "adb_enabled"
    }


    fun get(): String{
        return adbEnabled
    }
}