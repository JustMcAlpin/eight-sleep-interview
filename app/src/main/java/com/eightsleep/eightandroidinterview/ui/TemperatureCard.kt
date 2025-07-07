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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
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

/* Figma glow swatches */
private val coldGlow = Color(0xFF1F58DD)  // Temperature/-5 Below
private val warmGlow = Color(0xFFCA5469)  // Temperature/+1 Warm

/* ─── circular +/- helper ─── */
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
        IconButton(onClick = onClick, enabled = enabled) { icon() }
    }
}

/* ────────── main composable ────────── */
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

    /* glow colour based on temp */
    val targetGlow = when {
        centreTemp < 0  -> coldGlow
        centreTemp > 0  -> warmGlow
        else            -> Color.Transparent
    }.copy(alpha = .85f)

    val animatedAlpha by animateFloatAsState(targetGlow.alpha)
    val glowColour     = targetGlow.copy(alpha = animatedAlpha)

    val changing       = cardState == CardState.WARMING || cardState == CardState.COOLING
    val buttonsEnabled = cardState != CardState.OFF

    Card(
        modifier = Modifier.size(260.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF262626))
    ) {
        Box(Modifier.fillMaxSize()) {

            /* ── foreground UI ────────────────── */
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .zIndex(1f)
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
                        val sel = p == phase
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
                                    .height(1.dp)                          // thin bar
                                    .let { if (sel) it.fillMaxWidth() else it.width(40.dp) }
                                    .background(Color(0x885A5A5A))        // light grey for all
                            )
                            Text(
                                text  = if (sel) "Now"
                                else p.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (sel) Color.White else Color.Gray,
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
                } else Spacer(Modifier.height(16.dp))

                /* main value + controls */
                Row(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { onToggleOff() },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    CircleButton(buttonsEnabled, { onAdjust(-1) }) {
                        Icon(
                            Icons.Default.Remove, null,
                            tint = if (buttonsEnabled) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text  = if (cardState == CardState.OFF) "OFF" else centreTemp.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium
                    )

                    CircleButton(buttonsEnabled, { onAdjust(+1) }) {
                        Icon(
                            Icons.Default.Add, null,
                            tint = if (buttonsEnabled) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                /* tap-to-turn-on hint */
                if (cardState == CardState.OFF) {
                    Text(
                        text  = "TAP TO TURN ON",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
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
                } else Spacer(Modifier.height(8.dp))
            }

            /* ── radial glow background ───────── */
            Box(
                Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.medium)
                    .drawWithCache {
                        val radius = size.height * 0.8f
                        val centre = Offset(size.width / 2f, size.height)
                        val brush  = Brush.radialGradient(
                            colors   = listOf(glowColour, Color.Transparent),
                            center   = centre,
                            radius   = radius,
                            tileMode = TileMode.Clamp
                        )
                        onDrawBehind { drawRect(brush) }
                    }
                    .zIndex(0f)
            )
        }
    }
}

/* ---------- preview ---------- */
@Preview(showBackground = true)
@Composable
fun TemperaturePreview() {
    val demo = mapOf(
        TemperaturePhase.BEDTIME to -5,
        TemperaturePhase.NIGHT   to 0,
        TemperaturePhase.DAWN    to 1
    )
    TemperatureCard(
        phase           = TemperaturePhase.BEDTIME,
        temps           = demo,
        cardState       = CardState.OFF,
        onPhaseSelected = {},
        onAdjust        = {},
        onToggleOff     = {}
    )
}
