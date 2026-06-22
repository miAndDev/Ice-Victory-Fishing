package com.special.ascs.utils

sealed interface PolicyState {
    data object OnHideSplash: PolicyState
    data object OnShowMenu: PolicyState
    data class OnSave(val data:String): PolicyState
}