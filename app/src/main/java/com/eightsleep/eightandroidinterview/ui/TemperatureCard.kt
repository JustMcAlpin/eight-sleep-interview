package com.eightsleep.eightandroidinterview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ─── tiny enums so the file compiles ────────────────────────────────────────────
enum class TemperaturePhase { BEDTIME, NIGHT, DAWN }
enum class CardState { OFF, IDLE, COOLING, WARMING }

// ─── public composable you’ll call from MainActivity / ViewModel ───────────────
@Composable
fun TemperatureCard(
    phase: TemperaturePhase,
    temp: Int,
    cardState: CardState,
    onPhaseSelected: (TemperaturePhase) -> Unit,
    onAdjust: (Int) -> Unit,          // +1 / –1
    onToggleOff: () -> Unit
) {
    // TODO: replace this Box with the real layout
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray)
    ) {
        Text(
            text = if (cardState == CardState.OFF) "OFF" else temp.toString(),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}

// ─── preview so you can see something instantly in Android Studio ──────────────
@Preview(showBackground = true)
@Composable
fun TemperatureCardPreview() {
    TemperatureCard(
        phase = TemperaturePhase.BEDTIME,
        temp = -2,
        cardState = CardState.IDLE,
        onPhaseSelected = {},
        onAdjust = {},
        onToggleOff = {}
    )
}
