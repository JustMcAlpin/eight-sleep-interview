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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/* ───── tweakable underline widths ───────────────────────────────────────── */
private val UNDERLINE_SIDE   = 48.dp   // bedtime & dawn
private val UNDERLINE_CENTER = 92.dp   // night (centre)
/* ────────────────────────────────────────────────────────────────────────── */

enum class TemperaturePhase { BEDTIME, NIGHT, DAWN }
enum class CardState       { OFF, IDLE, COOLING, WARMING }

/* full Figma palette (clamp −5‥+5) */
private val tempColors = mapOf(
    5  to Color(0xFFDD4144), 4 to Color(0xFFD94F51), 3 to Color(0xFFD65A61),
    2  to Color(0xFFD3647C), 1 to Color(0xFFD06F8A), 0 to Color(0xFFB27FCA),
    -1 to Color(0xFF788CDD),-2 to Color(0xFF6484DD),-3 to Color(0xFF5075D8),
    -4 to Color(0xFF4F7FFF),-5 to Color(0xFF3A70FE)
)
private fun clamped(v: Int)      = v.coerceIn(-5, 5)
private fun colourFor(v: Int)    = tempColors[clamped(v)]!!
private fun miniTint(v: Int)     = colourFor(v).copy(alpha = .70f)

/* circular ± button */
@Composable
private fun CircleButton(
    modifier: Modifier = Modifier,
    enabled : Boolean,
    onClick : () -> Unit,
    icon    : @Composable () -> Unit
) {
    Box(
        modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White.copy(.10f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, enabled = enabled) { icon() }
    }
}

/* ─────────────────────────────── Card ───────────────────────────────────── */
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
    val baseGlow     = if (cardState == CardState.OFF) Color.Transparent
    else colourFor(centreTemp)
    val glowAlpha by animateFloatAsState(baseGlow.alpha)
    val glowColour = baseGlow.copy(alpha = glowAlpha)

    val controlsEnabled = cardState != CardState.OFF
    val isChanging      = cardState == CardState.WARMING || cardState == CardState.COOLING

    Card(
        modifier = Modifier.size(260.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF262626))
    ) {
        Box(Modifier.fillMaxSize()) {

            /* ─────────── UI layer ─────────── */
            Column(
                Modifier.fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .zIndex(1f)
            ) {
                /* header */
                Row(
                    Modifier.fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TEMPERATURE",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "open",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(Modifier.fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(.12f)))

                Spacer(Modifier.fillMaxWidth()
                    .height(8.dp)
                )

                /* phase row */
                Row(Modifier.fillMaxWidth()) {
                    TemperaturePhase.values().forEach { p ->
                        val selected = p == phase
                        Column(
                            Modifier.weight(1f)
                                .clickable { onPhaseSelected(p) }
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            /* mini number */
                            Text("${temps[p] ?: 0}",
                                color = miniTint(temps[p] ?: 0),
                                style = MaterialTheme.typography.labelMedium)

                            Spacer(Modifier.height(6.dp))

                            /* fixed-width underline */
                            val width = if (p == TemperaturePhase.NIGHT)
                                UNDERLINE_CENTER else UNDERLINE_SIDE
                            Box(Modifier.height(1.dp)
                                .width(width)
                                .background(Color(0x885A5A5A)))

                            Spacer(Modifier.height(6.dp))

                            /* label: always phase name */
                            Text(
                                p.name.lowercase().replaceFirstChar { it.titlecase() },
                                color = if (selected) Color.White else Color.Gray,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                /* banner */
                if (isChanging) {
                    Text(
                        if (cardState == CardState.WARMING) "WARMING TO" else "COOLING TO",
                        color  = Color.White,
                        style  = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(top = 6.dp, bottom = 2.dp)
                    )
                } else Spacer(Modifier.height(16.dp))

                /* centre value + controls */
                Box(
                    Modifier.weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onToggleOff() }
                ) {
                    CircleButton(
                        Modifier.align(Alignment.CenterStart),
                        controlsEnabled, { onAdjust(-1) }
                    ) {
                        Icon(Icons.Filled.Remove, null,
                            tint = if (controlsEnabled) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp))
                    }

                    val label = when {
                        cardState == CardState.OFF -> "OFF"
                        centreTemp > 0             -> "+$centreTemp"
                        else                       -> centreTemp.toString()
                    }
                    Text(label,
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.align(Alignment.Center))

                    CircleButton(
                        Modifier.align(Alignment.CenterEnd),
                        controlsEnabled, { onAdjust(+1) }
                    ) {
                        Icon(Icons.Filled.Add, null,
                            tint = if (controlsEnabled) Color.White else Color.Gray,
                            modifier = Modifier.size(26.dp))
                    }

                    if (cardState == CardState.OFF) {
                        Text(
                            "TAP TO TURN ON",
                            color = Color.White.copy(.6f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(bottom = 4.dp)
                        )
                    }
                }

                /* CURRENTLY AT */
                if (isChanging) {
                    Text(
                        "CURRENTLY AT ${temps[phase] ?: 0}",
                        color  = Color.White.copy(.6f),
                        style  = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                } else Spacer(Modifier.height(8.dp))
            }

            /* ─────────── glow layer ─────────── */
            Box(
                Modifier.matchParentSize()
                    .clip(MaterialTheme.shapes.medium)
                    .drawWithCache {
                        val radius = size.height * 0.8f
                        val centre = Offset(size.width / 2f, size.height)
                        val brush  = Brush.radialGradient(
                            listOf(glowColour, Color.Transparent),
                            centre, radius
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
