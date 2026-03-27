package com.example.companion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.companion.data.models.CreatureState

@Composable
fun StatsPanel(state: CreatureState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        StatRow(label = "Happiness", value = state.happiness)
        StatRow(label = "Friendliness", value = state.friendliness)
        StatRow(label = "Loyalty", value = state.loyalty)
        StatRow(label = "Love", value = state.love)
        StatRow(label = "Trust", value = state.trust)
    }
}

@Composable
fun StatRow(label: String, value: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: $value%")
        LinearProgressIndicator(
            progress = value / 100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
