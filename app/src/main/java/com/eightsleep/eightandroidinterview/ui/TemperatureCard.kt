package com.eightsleep.eightandroidinterview.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/* ─── enums ─── */
enum class TemperaturePhase { BEDTIME, NIGHT, DAWN }
enum class CardState       { OFF, IDLE, COOLING, WARMING }

/* tint for mini temps */
private fun miniTint(v: Int) = when {
    v < 0  -> Color(0xFF4CA3FF)
    v > 0  -> Color(0xFFFF6060)
    else   -> Color(0xFF8D8D8D)
}

/* constant glow hues */
private val coldGlow = Color(0xFF015BFF)
private val warmGlow = Color(0xFFFF1744)

/* ─── circular − / + helper ─── */
@Composable
private fun CircleButton(
    enabled: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Box(
        Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = .10f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,          // ⬅ named
            enabled = enabled           // ⬅ named (3rd param in Material-3 API)
        ) { icon() }
    }
}

/* ─────────── temperature card ─────────── */
@Composable
fun TemperatureCard(
    phase: TemperaturePhase,
    temps: Map<TemperaturePhase, Int>,
    cardState: CardState,
    onPhaseSelected: (TemperaturePhase) -> Unit,
    onAdjust: (Int) -> Unit,
    onToggleOff: () -> Unit
) {
    val centreTemp = temps[phase] ?: 0
    val targetGlow = when {
        centreTemp < 0  -> coldGlow.copy(alpha = .85f)
        centreTemp > 0  -> warmGlow.copy(alpha = .85f)
        else            -> Color.Transparent
    }
    val alpha by animateFloatAsState(targetGlow.alpha)
    val glowColour = targetGlow.copy(alpha = alpha)

    val changing   = cardState == CardState.WARMING || cardState == CardState.COOLING
    val buttonsOn  = cardState != CardState.OFF

    Card(
        modifier = Modifier.size(260.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF262626))
    ) {
        /* one Box = stack; Column above, glow Box below */
        Box(Modifier.fillMaxSize()) {

            /* ── UI content ───────────────────────── */
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .zIndex(1f)                      // above glow
            ) {

                /* header */
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "TEMPERATURE",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos, null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = .12f))
                )

                /* phase tabs */
                Row(Modifier.fillMaxWidth()) {
                    TemperaturePhase.values().forEach { p ->
                        val selected = p == phase
                        Column(
                            Modifier
                                .weight(1f)
                                .clickable { onPhaseSelected(p) }
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text  = "${temps[p] ?: 0}",
                                color = miniTint(temps[p] ?: 0),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Box(
                                Modifier
                                    .height(2.dp)
                                    .let {
                                        if (selected) it.fillMaxWidth()
                                        else it.width(40.dp)
                                    }
                                    .background(
                                        if (selected) Color.White
                                        else Color.Gray.copy(alpha = .55f)
                                    )
                            )
                            Text(
                                text  = if (selected) "Now"
                                else p.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (selected) Color.White else Color.Gray,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                /* status banner */
                if (changing) {
                    Text(
                        text  = if (cardState == CardState.WARMING) "WARMING TO" else "COOLING TO",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 6.dp, bottom = 2.dp)
                    )
                } else {
                    Spacer(Modifier.height(16.dp))
                }

                /* main value row */
                Row(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { onToggleOff() },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    CircleButton(buttonsOn, { onAdjust(-1) }) {
                        Icon(
                            Icons.Default.Remove, null,
                            tint = if (buttonsOn) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text  = if (cardState == CardState.OFF) "OFF" else centreTemp.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium
                    )

                    CircleButton(buttonsOn, { onAdjust(+1) }) {
                        Icon(
                            Icons.Default.Add, null,
                            tint = if (buttonsOn) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                /* currently at */
                if (changing) {
                    Text(
                        text  = "CURRENTLY AT ${temps[phase] ?: 0}",
                        color = Color.White.copy(alpha = .6f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                }
            }

            /* ── glow overlay ────────────────────── */
            Box(
                Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.medium)   // honour rounded corners
                    .background(
                        Brush.verticalGradient(
                            colors   = listOf(Color.Transparent, glowColour),
                            startY   = 0f,
                            endY     = Float.POSITIVE_INFINITY,
                            tileMode = TileMode.Clamp
                        )
                    )
                    .zIndex(0f)          // under UI, over grey bg
            )
        }
    }
}

/* --- Preview --- */
@Preview(showBackground = true)
@Composable
fun TemperaturePreview() {
    val demo = mapOf(
        TemperaturePhase.BEDTIME to -5,
        TemperaturePhase.NIGHT   to 0,
        TemperaturePhase.DAWN    to 1
    )
    TemperatureCard(
        phase          = TemperaturePhase.BEDTIME,
        temps          = demo,
        cardState      = CardState.IDLE,
        onPhaseSelected = {},
        onAdjust        = {},
        onToggleOff     = {}
    )
}
