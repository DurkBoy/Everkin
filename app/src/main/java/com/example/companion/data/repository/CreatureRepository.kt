package com.example.companion.data.repository

import com.example.companion.data.DataStoreManager
import com.example.companion.data.models.CreatureState
import com.example.companion.utils.AgeCalculator
import com.example.companion.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatureRepository(
    private val dataStore: DataStoreManager,
    private val applicationScope: CoroutineScope
) {
    private val _creatureState = MutableStateFlow(CreatureState())
    val creatureState: Flow<CreatureState> = _creatureState.asStateFlow()

    init {
        applicationScope.launch(Dispatchers.IO) {
            dataStore.creatureStateFlow.collect { initial ->
                _creatureState.update { initial }
            }
        }
    }

    suspend fun refreshStateFromTime() {
        val current = _creatureState.value
        val now = System.currentTimeMillis()

        if (!current.isHatched) return

        val (newStage, _) = AgeCalculator.getCurrentStageWithInfluence(
            current.birthTimestamp,
            now,
            (current.friendliness + current.loyalty + current.love + current.trust) / 4
        )

        val elapsedMillis = now - (current.lastInteractionTimestamp.takeIf { it > 0 } ?: now)
        val daysSinceLastInteraction = elapsedMillis / (24 * 60 * 60 * 1000.0)

        var newHappiness = current.happiness
        var newFriendliness = current.friendliness
        var newLoyalty = current.loyalty
        var newLove = current.love
        var newTrust = current.trust

        if (daysSinceLastInteraction > 1.0) {
            val decayFactor = (daysSinceLastInteraction * 0.05).coerceAtMost(0.5)
            newHappiness = (newHappiness - (10 * decayFactor).toInt()).coerceAtLeast(0)
            newFriendliness = (newFriendliness - (5 * decayFactor).toInt()).coerceAtLeast(0)
            newLoyalty = (newLoyalty - (5 * decayFactor).toInt()).coerceAtLeast(0)
            newLove = (newLove - (5 * decayFactor).toInt()).coerceAtLeast(0)
            newTrust = (newTrust - (5 * decayFactor).toInt()).coerceAtLeast(0)
        }

        val updated = current.copy(
            currentStage = newStage,
            happiness = newHappiness,
            friendliness = newFriendliness,
            loyalty = newLoyalty,
            love = newLove,
            trust = newTrust
        )
        _creatureState.update { updated }
        dataStore.saveCreatureState(updated)
    }

    suspend fun updateLastAppCloseTimestamp(timestamp: Long) {
        val current = _creatureState.value
        val updated = current.copy(lastAppCloseTimestamp = timestamp)
        _creatureState.update { updated }
        dataStore.saveCreatureState(updated)
    }

    suspend fun tapCrackedEgg() {
        val current = _creatureState.value
        if (current.currentStage == CreatureState.Stage.EGG) {
            val now = System.currentTimeMillis()
            val newState = current.copy(
                currentStage = CreatureState.Stage.CRACKED_EGG,
                lastInteractionTimestamp = now
            )
            _creatureState.update { newState }
            dataStore.saveCreatureState(newState)
        } else if (current.currentStage == CreatureState.Stage.CRACKED_EGG) {
            val now = System.currentTimeMillis()
            val newState = current.copy(
                isHatched = true,
                birthTimestamp = now,
                currentStage = CreatureState.Stage.BABY,
                lastInteractionTimestamp = now
            )
            _creatureState.update { newState }
            dataStore.saveCreatureState(newState)
        }
    }

    suspend fun tapInteraction() {
        val current = _creatureState.value
        if (!current.isHatched) {
            tapCrackedEgg()
            return
        }
        val now = System.currentTimeMillis()
        val updated = current.copy(
            happiness = (current.happiness + 5).coerceAtMost(100),
            friendliness = (current.friendliness + 3).coerceAtMost(100),
            loyalty = (current.loyalty + 2).coerceAtMost(100),
            love = (current.love + 4).coerceAtMost(100),
            trust = (current.trust + 3).coerceAtMost(100),
            lastInteractionTimestamp = now
        )
        _creatureState.update { updated }
        dataStore.saveCreatureState(updated)
    }

    suspend fun rubInteraction() {
        val current = _creatureState.value
        if (!current.isHatched) return
        val now = System.currentTimeMillis()
        val updated = current.copy(
            happiness = (current.happiness + 2).coerceAtMost(100),
            friendliness = (current.friendliness + 1).coerceAtMost(100),
            loyalty = (current.loyalty + 3).coerceAtMost(100),
            love = (current.love + 2).coerceAtMost(100),
            trust = (current.trust + 1).coerceAtMost(100),
            lastInteractionTimestamp = now
        )
        _creatureState.update { updated }
        dataStore.saveCreatureState(updated)
    }

    suspend fun feed() {
        val current = _creatureState.value
        if (!current.isHatched) return
        val now = System.currentTimeMillis()
        val updated = current.copy(
            happiness = (current.happiness + 10).coerceAtMost(100),
            trust = (current.trust + 5).coerceAtMost(100),
            lastInteractionTimestamp = now
        )
        _creatureState.update { updated }
        dataStore.saveCreatureState(updated)
    }

    suspend fun walk() {
        val current = _creatureState.value
        if (!current.isHatched) return
        val now = System.currentTimeMillis()
        val lastWalk = current.lastWalkTimestamp
        if (now - lastWalk < 60 * 60 * 1000) {
            return
        }
        val updated = current.copy(
            happiness = (current.happiness + 8).coerceAtMost(100),
            friendliness = (current.friendliness + 5).coerceAtMost(100),
            loyalty = (current.loyalty + 5).coerceAtMost(100),
            love = (current.love + 5).coerceAtMost(100),
            trust = (current.trust + 5).coerceAtMost(100),
            lastInteractionTimestamp = now,
            lastWalkTimestamp = now
        )
        _creatureState.update { updated }
        dataStore.saveCreatureState(updated)
    }

    fun getWelcomeMessage(elapsedSeconds: Long): String {
        return when {
            elapsedSeconds < 60 -> Constants.MESSAGES_WELCOME_BACK
            elapsedSeconds < 3600 -> Constants.MESSAGES_MISSED_YOU
            else -> Constants.MESSAGES_LONG_AWAY
        }
    }

    fun getReactionText(action: String, avgRelationship: Int): String {
        return when {
            avgRelationship < 30 -> when (action) {
                "tap" -> "..."
                "rub" -> "..."
                "feed" -> "..."
                else -> "..."
            }
            avgRelationship < 70 -> when (action) {
                "tap" -> "😊"
                "rub" -> "😊"
                "feed" -> "Yum! 😊"
                else -> "😊"
            }
            else -> when (action) {
                "tap" -> "🥰"
                "rub" -> "🥰"
                "feed" -> "Yum! 🥰"
                else -> "🥰"
            }
        }
    }

    fun getCreatureBehaviorLevel(stats: CreatureState): String {
        val avg = (stats.friendliness + stats.loyalty + stats.love + stats.trust) / 4
        return when {
            avg < 30 -> "distant"
            avg < 70 -> "neutral"
            else -> "affectionate"
        }
    }
}
