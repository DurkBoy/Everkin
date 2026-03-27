package com.example.companion.data.models

data class CreatureState(
    val isHatched: Boolean = false,
    val birthTimestamp: Long = 0L,
    val currentStage: Stage = Stage.EGG,
    val happiness: Int = 70,
    val friendliness: Int = 50,
    val loyalty: Int = 50,
    val love: Int = 50,
    val trust: Int = 50,
    val lastInteractionTimestamp: Long = 0L,
    val lastWalkTimestamp: Long = 0L,
    val lastAppCloseTimestamp: Long = 0L
) {
    enum class Stage {
        EGG, CRACKED_EGG, BABY, CHILD, TEEN, ADULT
    }
}
