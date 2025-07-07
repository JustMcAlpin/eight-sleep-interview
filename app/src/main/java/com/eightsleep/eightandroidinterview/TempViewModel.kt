package com.eightsleep.eightandroidinterview

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eightsleep.eightandroidinterview.ui.CardState
import com.eightsleep.eightandroidinterview.ui.TemperaturePhase
import com.eightsleep.eightandroidinterview.data.FakePadService
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


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

    // ── NEW state for sensed temp ──
    val currentTemps = mutableStateMapOf(
        TemperaturePhase.BEDTIME to -2,
        TemperaturePhase.NIGHT   to -4,
        TemperaturePhase.DAWN    to  4
    )

    /* ───────── intent functions ───────── */
    fun selectPhase(p: TemperaturePhase) {
        selected.value = p
    }

    private var driftJob: Job? = null      // add at top

    fun adjust(delta: Int) {
        if (cardState.value == CardState.OFF) return
        val newTemp = (temps[selected.value] ?: 0) + delta
        if (newTemp !in -10..10) return

        temps[selected.value] = newTemp
        cardState.value = if (delta > 0) CardState.WARMING else CardState.COOLING

        driftJob?.cancel()                 // stop any previous drift
        simulateHardware(selected.value)   // start fresh
    }


    /** Centre label tap toggles ON ⇄ OFF. */
    fun toggleOff() {
        cardState.value = if (cardState.value == CardState.OFF)
            CardState.IDLE else CardState.OFF
    }

    private fun simulateHardware(phase: TemperaturePhase) {
        driftJob = viewModelScope.launch {
            var current = currentTemps[phase] ?: 0
            val target  = temps[phase]        ?: 0

            while (current != target) {
                current = FakePadService.nudge(current, target)
                currentTemps[phase] = current
            }
            // reached target → idle
            cardState.value = CardState.IDLE
        }
    }

}
