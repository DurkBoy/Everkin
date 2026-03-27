package com.example.companion.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.companion.data.models.CreatureState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "creature_prefs")

class DataStoreManager(private val context: Context) {

    private val IS_HATCHED = booleanPreferencesKey("is_hatched")
    private val BIRTH_TIMESTAMP = longPreferencesKey("birth_timestamp")
    private val CURRENT_STAGE_ORDINAL = intPreferencesKey("current_stage_ordinal")
    private val HAPPINESS = intPreferencesKey("happiness")
    private val FRIENDLINESS = intPreferencesKey("friendliness")
    private val LOYALTY = intPreferencesKey("loyalty")
    private val LOVE = intPreferencesKey("love")
    private val TRUST = intPreferencesKey("trust")
    private val LAST_INTERACTION = longPreferencesKey("last_interaction")
    private val LAST_WALK = longPreferencesKey("last_walk")
    private val LAST_APP_CLOSE = longPreferencesKey("last_app_close")

    val creatureStateFlow: Flow<CreatureState> = context.dataStore.data.map { prefs ->
        CreatureState(
            isHatched = prefs[IS_HATCHED] ?: false,
            birthTimestamp = prefs[BIRTH_TIMESTAMP] ?: 0L,
            currentStage = CreatureState.Stage.values().getOrElse(
                prefs[CURRENT_STAGE_ORDINAL] ?: 0
            ) { CreatureState.Stage.EGG },
            happiness = prefs[HAPPINESS] ?: 70,
            friendliness = prefs[FRIENDLINESS] ?: 50,
            loyalty = prefs[LOYALTY] ?: 50,
            love = prefs[LOVE] ?: 50,
            trust = prefs[TRUST] ?: 50,
            lastInteractionTimestamp = prefs[LAST_INTERACTION] ?: 0L,
            lastWalkTimestamp = prefs[LAST_WALK] ?: 0L,
            lastAppCloseTimestamp = prefs[LAST_APP_CLOSE] ?: 0L
        )
    }

    suspend fun saveCreatureState(state: CreatureState) {
        context.dataStore.edit { prefs ->
            prefs[IS_HATCHED] = state.isHatched
            prefs[BIRTH_TIMESTAMP] = state.birthTimestamp
            prefs[CURRENT_STAGE_ORDINAL] = state.currentStage.ordinal
            prefs[HAPPINESS] = state.happiness.coerceIn(0, 100)
            prefs[FRIENDLINESS] = state.friendliness.coerceIn(0, 100)
            prefs[LOYALTY] = state.loyalty.coerceIn(0, 100)
            prefs[LOVE] = state.love.coerceIn(0, 100)
            prefs[TRUST] = state.trust.coerceIn(0, 100)
            prefs[LAST_INTERACTION] = state.lastInteractionTimestamp
            prefs[LAST_WALK] = state.lastWalkTimestamp
            prefs[LAST_APP_CLOSE] = state.lastAppCloseTimestamp
        }
    }
}
