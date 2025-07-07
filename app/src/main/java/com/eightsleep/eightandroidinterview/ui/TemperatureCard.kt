package com.eightsleep.eightandroidinterview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// ─── tiny enums so the file compiles ────────────────────────────────────────────
enum class TemperaturePhase { BEDTIME, NIGHT, DAWN }
enum class CardState { OFF, IDLE, COOLING, WARMING }

@Composable
fun TemperatureCard(
    phase: TemperaturePhase,
    temp: Int,
    cardState: CardState,
    onPhaseSelected: (TemperaturePhase) -> Unit,
    onAdjust: (Int) -> Unit,
    onToggleOff: () -> Unit
) {
    Card(   // nicer rounded container
        modifier = Modifier.size(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262626))
    ) {
        Column {
            // ── PHASE ROW ──────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                TemperaturePhase.values().forEach { p ->
                    Text(
                        text = p.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPhaseSelected(p) }
                            .padding(vertical = 8.dp),
                        color = if (p == phase) Color.White else Color.Gray,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── CENTER VALUE / OFF ─────────────────
            Box(
                Modifier.fillMaxWidth()
                .clickable { onToggleOff() }
            ) {
                Text(
                    text = if (cardState == CardState.OFF) "OFF" else temp.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── +/- ROW (will disable when OFF) ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { onAdjust(-1) },
                    enabled = cardState != CardState.OFF
                ) { Icon(Icons.Default.Remove, contentDescription = "decrease") }

                IconButton(
                    onClick = { onAdjust(+1) },
                    enabled = cardState != CardState.OFF
                ) { Icon(Icons.Default.Add, contentDescription = "increase") }
            }
        }
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
