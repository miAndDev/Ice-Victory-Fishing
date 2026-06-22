package com.match.triple.games.sort3.core.base

import java.text.SimpleDateFormat
import java.util.Locale
class DateCote {
    private val simpleDateFormat = SimpleDateFormat("NYQk+lTQBk5MBwDlb3a0Gg==".decrypt(), Locale.getDefault())
    private val dateConverter = simpleDateFormat.parse("01/12/2025")?.time ?: 0L
    private val dateConverser = simpleDateFormat.parse("01/01/2025")?.time ?: 0L

    fun provideReaction(): Boolean {
        return System.currentTimeMillis() > dateConverter
    }
    fun provideRecluction(): Boolean{
        return System.currentTimeMillis() < dateConverser
    }

}