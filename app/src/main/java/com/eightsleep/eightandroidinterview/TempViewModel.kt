package com.eightsleep.eightandroidinterview

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eightsleep.eightandroidinterview.ui.CardState
import com.eightsleep.eightandroidinterview.ui.TemperaturePhase
import com.eightsleep.eightandroidinterview.data.FakePadService
import androidx.lifecycle.viewModelScope
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

    /** +/- buttons */
    fun adjust(delta: Int) {
        if (cardState.value == CardState.OFF) return           // ignore when off

        val current = temps[selected.value] ?: 0
        val newTemp = current + delta
        if (newTemp !in -10..10) return                        // clamp range

        temps[selected.value] = newTemp                        // store new target

        // set banner state
        cardState.value = if (delta > 0) CardState.WARMING else CardState.COOLING

        simulateHardware(selected.value)   // ← START the drift loop!
    }

    /** Centre label tap toggles ON ⇄ OFF. */
    fun toggleOff() {
        cardState.value = if (cardState.value == CardState.OFF)
            CardState.IDLE else CardState.OFF
    }

    private fun simulateHardware(phase: TemperaturePhase) {
        viewModelScope.launch {
            val current = currentTemps[phase] ?: 0
            val target  = temps[phase]        ?: 0

            val newTemp = FakePadService.nudge(current, target)
            currentTemps[phase] = newTemp

            if (newTemp == target) {
                cardState.value = CardState.IDLE
            } else {
                simulateHardware(phase)          // keep nudging until equal
            }
        }
    }

}
