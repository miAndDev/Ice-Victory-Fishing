package com.special.ascs

import com.anor.security.StringShield
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.special.ascs.utils.Extender
import com.special.ascs.utils.PolicyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds

@StringShield
class PolicyScreenViewModel(
) : ViewModel() {
    val extender = Extender(onShowMenu = {
        viewModelScope.launch(Dispatchers.IO) {
            navState.emit(PolicyState.OnShowMenu)
        }
    }, onHideSplash = {
        viewModelScope.launch(Dispatchers.IO) {
            navState.emit(PolicyState.OnHideSplash)
        }
    }, onSaveData = {
        viewModelScope.launch(Dispatchers.IO) {
            navState.emit(PolicyState.OnSave(it))
        }
    })

    private val _openHandler = MutableSharedFlow<Actions>()
    val openHandler = _openHandler.asSharedFlow()
    val perm = "ENPYGASvtnkkU5WTa/O0QYHcoY4vdlXfZcjq+ci+Pn0=".decrypt()
    val navState = MutableSharedFlow<PolicyState>()

    init {
        init()
    }


    private val _isInternetReachable = MutableStateFlow(true)
    val isInternetReachable = _isInternetReachable.asStateFlow()


    private fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                runCatching {
                    _isInternetReachable.emit(
                        runCatching {
                            InetAddress.getByName("google.com").isReachable(10000)
                        }.getOrDefault(false)
                    )
                }
                delay(3.seconds)
            }
        }
    }

    private var lastLoaded = 0L

    fun openWithResult(action: Actions) {
        if (System.currentTimeMillis() - lastLoaded > 100L) {
            viewModelScope.launch {
                _openHandler.emit(action)
            }
            lastLoaded = System.currentTimeMillis()
        }
    }

}