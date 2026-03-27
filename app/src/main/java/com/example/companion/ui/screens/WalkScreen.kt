package com.example.companion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.companion.ui.components.CreatureImage
import com.example.companion.viewmodel.MainViewModel
import com.example.companion.viewmodel.WalkViewModel

@Composable
fun WalkScreen(
    viewModel: WalkViewModel,
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    val state by mainViewModel.creatureState.collectAsState()
    val walkMessage by viewModel.walkMessage.collectAsState()
    val canWalk by viewModel.canWalk.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshCooldown()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC8E6C9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌳 Park 🌳",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CreatureImage(stage = state.currentStage, modifier = Modifier.size(200.dp))

        Spacer(modifier = Modifier.height(16.dp))

        if (walkMessage.isNotBlank()) {
            Text(text = walkMessage, fontSize = 18.sp, color = Color.DarkGray)
        } else {
            Text("Take your companion for a walk to boost stats!")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.performWalk() },
            enabled = canWalk
        ) {
            Text("Go for a walk")
        }

        if (!canWalk) {
            Text(
                text = "Your companion is resting. Come back in an hour.",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back Home")
        }
    }
}
