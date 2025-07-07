package com.eightsleep.eightandroidinterview

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eightsleep.eightandroidinterview.ui.CardState
import com.eightsleep.eightandroidinterview.ui.TemperaturePhase

class TempViewModel : ViewModel() {

    /* ───────── public UI state ───────── */
    val selected  = mutableStateOf(TemperaturePhase.BEDTIME)
    val cardState = mutableStateOf(CardState.IDLE)

    /** Per-phase temperature settings (–10 … +10). */
    val temps = mutableStateMapOf(
        TemperaturePhase.BEDTIME to -2,
        TemperaturePhase.NIGHT   to  -4,
        TemperaturePhase.DAWN    to  4
    )

    /* ───────── intent functions ───────── */
    fun selectPhase(p: TemperaturePhase) {
        selected.value = p
    }

    /** +/- buttons */
    fun adjust(delta: Int) {
        // ignore when OFF
        if (cardState.value == CardState.OFF) return

        val current  = temps[selected.value] ?: 0
        val newTemp  = current + delta
        if (newTemp !in -10..10) return                    // clamp range

        temps[selected.value] = newTemp                    // store new target

        // update banner state so card shows WARMING / COOLING
        cardState.value = when {
            delta > 0  -> CardState.WARMING
            delta < 0  -> CardState.COOLING
            else       -> CardState.IDLE                   // unlikely but safe
        }
    }

    /** Centre label tap toggles ON ⇄ OFF. */
    fun toggleOff() {
        cardState.value = if (cardState.value == CardState.OFF)
            CardState.IDLE else CardState.OFF
    }
}
