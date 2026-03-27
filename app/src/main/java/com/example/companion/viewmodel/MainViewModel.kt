package com.example.companion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.companion.data.repository.CreatureRepository
import com.example.companion.data.models.CreatureState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CreatureRepository
) : ViewModel() {

    private val _creatureState = MutableStateFlow(CreatureState())
    val creatureState: StateFlow<CreatureState> = _creatureState.asStateFlow()

    private val _welcomeMessage = MutableStateFlow("")
    val welcomeMessage: StateFlow<String> = _welcomeMessage.asStateFlow()

    private val _reactionText = MutableStateFlow("")
    val reactionText: StateFlow<String> = _reactionText.asStateFlow()

    init {
        viewModelScope.launch {
            repository.creatureState.collect { state ->
                _creatureState.update { state }
            }
        }
        viewModelScope.launch {
            repository.refreshStateFromTime()
            val lastClose = _creatureState.value.lastAppCloseTimestamp
            if (lastClose > 0) {
                val elapsedSeconds = (System.currentTimeMillis() - lastClose) / 1000
                if (elapsedSeconds > 30) {
                    _welcomeMessage.update { repository.getWelcomeMessage(elapsedSeconds) }
                }
            }
            repository.updateLastAppCloseTimestamp(System.currentTimeMillis())
        }
    }

    fun onTap() {
        viewModelScope.launch {
            repository.tapInteraction()
            val state = repository.creatureState.first()
            val avg = (state.friendliness + state.loyalty + state.love + state.trust) / 4
            _reactionText.update { repository.getReactionText("tap", avg) }
            delay(1000)
            _reactionText.update { "" }
        }
    }

    fun onRub() {
        viewModelScope.launch {
            repository.rubInteraction()
            val state = repository.creatureState.first()
            val avg = (state.friendliness + state.loyalty + state.love + state.trust) / 4
            _reactionText.update { repository.getReactionText("rub", avg) }
            delay(1000)
            _reactionText.update { "" }
        }
    }

    fun onFeed() {
        viewModelScope.launch {
            repository.feed()
            val state = repository.creatureState.first()
            val avg = (state.friendliness + state.loyalty + state.love + state.trust) / 4
            _reactionText.update { repository.getReactionText("feed", avg) }
            delay(1000)
            _reactionText.update { "" }
        }
    }

    fun onWalk() {
        viewModelScope.launch {
            repository.walk()
            val state = repository.creatureState.first()
            val avg = (state.friendliness + state.loyalty + state.love + state.trust) / 4
            _reactionText.update { "Walk! ${repository.getReactionText("walk", avg)}" }
            delay(1000)
            _reactionText.update { "" }
        }
    }

    fun clearWelcomeMessage() {
        _welcomeMessage.update { "" }
    }
}
