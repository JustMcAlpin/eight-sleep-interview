package com.eightsleep.eightandroidinterview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ── enums (keep / move elsewhere) ─────────────────────────────────────────────
enum class TemperaturePhase { BEDTIME, NIGHT, DAWN }
enum class CardState       { OFF, IDLE, COOLING, WARMING }

// helper to tint mini temps
private fun tempColor(v: Int): Color = when {
    v < 0  -> Color(0xFF6AB4FF)           // blue
    v > 0  -> Color(0xFFFF6A6A)           // red
    else   -> Color(0xFFB0B0B0)           // neutral
}

@Composable
fun TemperatureCard(
    phase: TemperaturePhase,
    temps: Map<TemperaturePhase, Int>,
    cardState: CardState,
    onPhaseSelected: (TemperaturePhase) -> Unit,
    onAdjust: (Int) -> Unit,
    onToggleOff: () -> Unit
) {
    val centerTemp = temps[phase] ?: 0
    val bgColor    = Color(0xFF262626)        // neutral bg (tint later if desired)

    Card(
        modifier = Modifier.size(240.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            // ───────────── HEADER ──────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = "TEMPERATURE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
            // thin divider
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            // ───────────── PHASE ROW ───────────────────────────────────────
            Row(Modifier.fillMaxWidth()) {
                TemperaturePhase.values().forEach { p ->
                    val isSelected = p == phase
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPhaseSelected(p) }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // mini number
                        Text(
                            text  = "${temps[p] ?: 0}",
                            color = tempColor(temps[p] ?: 0),
                            style = MaterialTheme.typography.bodySmall
                        )
                        // underline (always visible)
                        Box(
                            Modifier
                                .height(2.dp)
                                .let { base ->
                                    if (isSelected) base.fillMaxWidth()
                                    else base.width(36.dp)
                                }
                                .background(
                                    if (isSelected) Color.White
                                    else Color.Gray.copy(alpha = 0.6f)
                                )
                        )
                        // label
                        val label = if (isSelected) "Now"
                        else p.name.lowercase().replaceFirstChar { it.uppercase() }
                        Text(
                            text  = label,
                            color = if (isSelected) Color.White else Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ───────────── STATUS BANNER ───────────────────────────────────
            if (cardState == CardState.COOLING || cardState == CardState.WARMING) {
                Text(
                    text  = if (cardState == CardState.COOLING) "COOLING TO" else "WARMING TO",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                )
            }

            // ───────────── MAIN ( –  value  + ) ────────────────────────────
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onToggleOff() },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAdjust(-1) },
                    enabled = cardState != CardState.OFF
                ) {
                    Icon(
                        Icons.Default.Remove, null,
                        tint = if (cardState != CardState.OFF) Color.White else Color.Gray
                    )
                }
                Text(
                    text  = if (cardState == CardState.OFF) "OFF" else centerTemp.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                IconButton(
                    onClick = { onAdjust(+1) },
                    enabled = cardState != CardState.OFF
                ) {
                    Icon(
                        Icons.Default.Add, null,
                        tint = if (cardState != CardState.OFF) Color.White else Color.Gray
                    )
                }
            }

            // ───────────── CURRENTLY AT BANNER ─────────────────────────────
            if (cardState == CardState.COOLING || cardState == CardState.WARMING) {
                val currentTemp = temps[phase] ?: 0
                Text(
                    text  = "CURRENTLY AT $currentTemp",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )
            } else {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ───────────── PREVIEW ────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun TemperatureCardPreview() {
    val demoTemps = mapOf(
        TemperaturePhase.BEDTIME to 1,
        TemperaturePhase.NIGHT   to 0,
        TemperaturePhase.DAWN    to 1
    )
    TemperatureCard(
        phase          = TemperaturePhase.BEDTIME,
        temps          = demoTemps,
        cardState      = CardState.WARMING,
        onPhaseSelected = {},
        onAdjust        = {},
        onToggleOff     = {}
    )
}
