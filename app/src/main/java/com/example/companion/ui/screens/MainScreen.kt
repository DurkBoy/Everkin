package com.example.companion.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.companion.ui.components.CreatureImage
import com.example.companion.ui.components.StatsPanel
import com.example.companion.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onWalkClick: () -> Unit
) {
    val state by viewModel.creatureState.collectAsState()
    val welcomeMessage by viewModel.welcomeMessage.collectAsState()
    val reactionText by viewModel.reactionText.collectAsState()

    LaunchedEffect(welcomeMessage) {
        if (welcomeMessage.isNotBlank()) {
            delay(3000)
            viewModel.clearWelcomeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Virtual Companion") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (welcomeMessage.isNotBlank()) {
                Text(
                    text = welcomeMessage,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(220.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { viewModel.onTap() }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                viewModel.onRub()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                CreatureImage(stage = state.currentStage)
            }

            if (reactionText.isNotBlank()) {
                Text(
                    text = reactionText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { viewModel.onFeed() }) {
                    Text("Feed")
                }
                Button(onClick = onWalkClick) {
                    Text("Walk")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StatsPanel(state = state)
        }
    }
}
