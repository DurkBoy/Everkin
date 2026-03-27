package com.example.companion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.companion.data.repository.CreatureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalkViewModel(
    private val repository: CreatureRepository
) : ViewModel() {

    private val _walkMessage = MutableStateFlow("")
    val walkMessage: StateFlow<String> = _walkMessage.asStateFlow()

    private val _canWalk = MutableStateFlow(true)
    val canWalk: StateFlow<Boolean> = _canWalk.asStateFlow()

    private var cooldownJob: kotlinx.coroutines.Job? = null

    fun refreshCooldown() {
        viewModelScope.launch {
            val current = repository.creatureState.first()
            val now = System.currentTimeMillis()
            val lastWalk = current.lastWalkTimestamp
            val remaining = (lastWalk + 60 * 60 * 1000) - now
            if (remaining > 0) {
                _canWalk.value = false
                cooldownJob?.cancel()
                cooldownJob = viewModelScope.launch {
                    delay(remaining)
                    _canWalk.value = true
                }
            } else {
                _canWalk.value = true
            }
        }
    }

    fun performWalk() {
        viewModelScope.launch {
            if (!_canWalk.value) {
                _walkMessage.value = "Please wait, your companion is resting..."
                delay(2000)
                _walkMessage.value = ""
                return@launch
            }
            repository.walk()
            _walkMessage.value = "The walk made me happy!"
            delay(2000)
            _walkMessage.value = ""
            refreshCooldown()
        }
    }

    fun clearMessage() {
        _walkMessage.value = ""
    }
}
