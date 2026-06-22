package com.special.ascs

sealed interface Actions {
    data object OpenG: Actions
    data object OpenC:Actions

}