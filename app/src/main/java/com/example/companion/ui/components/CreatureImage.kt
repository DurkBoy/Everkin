package com.example.companion.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.companion.R
import com.example.companion.data.models.CreatureState

@Composable
fun CreatureImage(stage: CreatureState.Stage, modifier: Modifier = Modifier) {
    val drawableId = when (stage) {
        CreatureState.Stage.EGG -> R.drawable.ic_egg
        CreatureState.Stage.CRACKED_EGG -> R.drawable.ic_cracked_egg
        CreatureState.Stage.BABY -> R.drawable.ic_baby
        CreatureState.Stage.CHILD -> R.drawable.ic_child
        CreatureState.Stage.TEEN -> R.drawable.ic_teen
        CreatureState.Stage.ADULT -> R.drawable.ic_adult
    }
    Icon(
        painter = painterResource(id = drawableId),
        contentDescription = "Creature",
        modifier = modifier.size(200.dp)
    )
}
