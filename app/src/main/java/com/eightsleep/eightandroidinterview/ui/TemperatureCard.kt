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

/* ─── enums ───────────────────────────────────────────── */
enum class TemperaturePhase { BEDTIME, NIGHT, DAWN }
enum class CardState       { OFF, IDLE, COOLING, WARMING }

/* ─── full temp-colour palette (Figma) ─── */
private val tempColors = mapOf(
    5 to Color(0xFFDD4144),
    4 to Color(0xFFD94F51),
    3 to Color(0xFFD65A61),
    2 to Color(0xFFD3647C),
    1 to Color(0xFFD06F8A),
    0 to Color(0xFFB27FCA),
    -1 to Color(0xFF788CDD),
    -2 to Color(0xFF6484DD),
    -3 to Color(0xFF5075D8),
    -4 to Color(0xFF4F7FFF),
    -5 to Color(0xFF3A70FE)
)
private fun clamped(v: Int) = v.coerceIn(-5, 5)
private fun colourFor(v: Int) = tempColors[clamped(v)]!!

/* ─── circular +/- helper ─── */
@Composable
private fun CircleButton(
    modifier : Modifier = Modifier,
    enabled  : Boolean,
    onClick  : () -> Unit,
    icon     : @Composable () -> Unit
) {
    Box(
        modifier
            .size(52.dp)
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
    phase          : TemperaturePhase,
    temps          : Map<TemperaturePhase, Int>,
    cardState      : CardState,
    onPhaseSelected: (TemperaturePhase) -> Unit,
    onAdjust       : (Int) -> Unit,
    onToggleOff    : () -> Unit
) {
    val centreTemp   = temps[phase] ?: 0

    /* glow driven by palette */
    val glowTarget = colourFor(centreTemp).copy(alpha = .85f)
    val alpha by animateFloatAsState(glowTarget.alpha)
    val glowColour = glowTarget.copy(alpha = alpha)

    val changing       = cardState == CardState.WARMING || cardState == CardState.COOLING
    val controlsOn     = cardState != CardState.OFF

    Card(
        modifier = Modifier.size(260.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF262626))
    ) {
        Box(Modifier.fillMaxSize()) {

            /* ───────────── foreground ───────────── */
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
                        "TEMPERATURE",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        null,
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

                /* tabs */
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
                                "${temps[p] ?: 0}",
                                color = colourFor(temps[p] ?: 0),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Box(
                                Modifier
                                    .height(1.dp)
                                    .let { if (sel) it.fillMaxWidth() else it.width(40.dp) }
                                    .background(Color(0x885A5A5A))
                            )
                            Text(
                                if (sel) "Now"
                                else p.name.lowercase().replaceFirstChar { it.titlecase() },
                                color = if (sel) Color.White else Color.Gray,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                /* banner */
                if (changing) {
                    Text(
                        if (cardState == CardState.WARMING) "WARMING TO" else "COOLING TO",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 6.dp, bottom = 2.dp)
                    )
                } else Spacer(Modifier.height(16.dp))

                /* controls row */
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .clickable { onToggleOff() }
                ) {
                    CircleButton(
                        Modifier.align(Alignment.CenterStart),
                        controlsOn,
                        { onAdjust(-1) }
                    ) {
                        Icon(
                            Icons.Filled.Remove, null,
                            tint = if (controlsOn) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // centre label
                    val label =
                        if (cardState == CardState.OFF) "OFF"
                        else if (centreTemp > 0) "+$centreTemp"
                        else centreTemp.toString()

                    Text(
                        label,
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    CircleButton(
                        Modifier.align(Alignment.CenterEnd),
                        controlsOn,
                        { onAdjust(+1) }
                    ) {
                        Icon(
                            Icons.Filled.Add, null,
                            tint = if (controlsOn) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    if (cardState == CardState.OFF) {
                        Text(
                            "TAP TO TURN ON",
                            color = Color.White.copy(alpha = .6f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 4.dp)
                        )
                    }
                }

                /* currently at */
                if (changing) {
                    Text(
                        "CURRENTLY AT ${temps[phase] ?: 0}",
                        color = Color.White.copy(alpha = .6f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                } else Spacer(Modifier.height(8.dp))
            }

            /* ───────── radial glow ───────── */
            Box(
                Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.medium)
                    .drawWithCache {
                        val radius = size.height * 0.8f
                        val centre = Offset(size.width / 2f, size.height)
                        val brush  = Brush.radialGradient(
                            listOf(glowColour, Color.Transparent),
                            center = centre,
                            radius = radius
                        )
                        onDrawBehind { drawRect(brush) }
                    }
            )
        }
    }
}

/* preview */
@Preview(showBackground = true)
@Composable
fun TemperaturePreview() {
    val demo = mapOf(
        TemperaturePhase.BEDTIME to -2,
        TemperaturePhase.NIGHT   to 0,
        TemperaturePhase.DAWN    to 5
    )
    TemperatureCard(
        phase           = TemperaturePhase.BEDTIME,
        temps           = demo,
        cardState       = CardState.IDLE,
        onPhaseSelected = {},
        onAdjust        = {},
        onToggleOff     = {}
    )
}
