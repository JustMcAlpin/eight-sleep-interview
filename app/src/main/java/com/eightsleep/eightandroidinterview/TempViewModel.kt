package com.eightsleep.eightandroidinterview

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eightsleep.eightandroidinterview.ui.CardState
import com.eightsleep.eightandroidinterview.ui.TemperaturePhase

class TempViewModel : ViewModel() {

    // ---------- public UI state ----------
    val selected   = mutableStateOf(TemperaturePhase.BEDTIME)
    val cardState  = mutableStateOf(CardState.IDLE)

    // temps per phase, start with demo values
    val temps = mutableStateMapOf(
        TemperaturePhase.BEDTIME to -2,
        TemperaturePhase.NIGHT   to 0,
        TemperaturePhase.DAWN    to 1
    )

    // ---------- intent functions ----------
    fun selectPhase(p: TemperaturePhase) {
        selected.value = p
    }

    fun adjust(delta: Int) {
        if (cardState.value == CardState.OFF) return          // disabled when off
        val newTemp = (temps[selected.value] ?: 0) + delta
        if (newTemp in -10..10) temps[selected.value] = newTemp
    }

    fun toggleOff() {
        cardState.value =
            if (cardState.value == CardState.OFF) CardState.IDLE else CardState.OFF
    }
}
