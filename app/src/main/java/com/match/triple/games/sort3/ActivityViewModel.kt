package com.match.triple.games.sort3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.match.triple.games.sort3.core.base.SignalCore
import com.match.triple.games.sort3.domain.creation.CreationDomain
import com.match.triple.games.sort3.domain.datastore.SavedDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ActivityViewModel(
    private val creationDomain: CreationDomain,
    private val savedDomain: SavedDomain,
    private val signalCore: SignalCore
) : ViewModel() {

    init {
        init()
    }

    private val _info = MutableSharedFlow<String>(extraBufferCapacity = 1, replay = 1)
    val info = _info.asSharedFlow()

    private val _showSplash = MutableStateFlow(true)
    val showSplash = _showSplash.asStateFlow()

    private val _showMenu = MutableStateFlow(false)
    val showMenu = _showMenu.asStateFlow()


    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val user = savedDomain.userSaved.first()
                if (!user) {
                    val login = savedDomain.login.first().ifEmpty {
                        creationDomain.invoke()
                    }
                    Log.d("LINK_DATA", login)
                    signalCore.init(savedDomain.cachedUser.first())
                    _info.emit(login)
                }
            }.onFailure {
                showMenu()
                hideSplash()
            }
        }
    }

    fun hideSplash() {
        _showSplash.value = false
    }

    fun showMenu() {
        _showMenu.value = true
    }


    fun saveInfo(info:String){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("LINK_DATA", "save $info")
            savedDomain.saveLogin(info)
        }
    }

}