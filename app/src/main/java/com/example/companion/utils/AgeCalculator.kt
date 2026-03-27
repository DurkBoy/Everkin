package com.example.companion.utils

import com.example.companion.data.models.CreatureState.Stage
import kotlin.math.max
import kotlin.math.min

object AgeCalculator {
    private const val MIN_TOTAL_DAYS = 28
    private const val MAX_TOTAL_DAYS = 42
    private const val DEFAULT_TOTAL_DAYS = 35

    private const val BABY_PROP = 0.25
    private const val CHILD_PROP = 0.25
    private const val TEEN_PROP = 0.5

    fun getCurrentStageWithInfluence(birthTimestamp: Long, currentTime: Long, averageRelationship: Int): Pair<Stage, Boolean> {
        if (birthTimestamp == 0L) return Pair(Stage.EGG, false)

        val elapsed = currentTime - birthTimestamp

        val factor = (100 - averageRelationship) / 100.0
        val totalDays = MIN_TOTAL_DAYS + (MAX_TOTAL_DAYS - MIN_TOTAL_DAYS) * factor
        val totalMs = (totalDays * 24 * 60 * 60 * 1000).toLong()

        val babyMs = (totalMs * BABY_PROP).toLong()
        val childMs = (totalMs * CHILD_PROP).toLong()
        val teenMs = (totalMs * TEEN_PROP).toLong()

        val stage = when {
            elapsed < babyMs -> Stage.BABY
            elapsed < babyMs + childMs -> Stage.CHILD
            elapsed < babyMs + childMs + teenMs -> Stage.TEEN
            else -> Stage.ADULT
        }

        return Pair(stage, false)
    }

    fun getStageAgeString(stage: Stage, birthTimestamp: Long, currentTime: Long): String {
        if (birthTimestamp == 0L) return "Just born"
        val days = (currentTime - birthTimestamp) / (24 * 60 * 60 * 1000)
        return when (stage) {
            Stage.BABY -> "Baby ($days days)"
            Stage.CHILD -> "Child ($days days)"
            Stage.TEEN -> "Teen ($days days)"
            Stage.ADULT -> "Adult ($days days)"
            Stage.EGG -> "Egg"
            Stage.CRACKED_EGG -> "Cracked Egg"
        }
    }
}
