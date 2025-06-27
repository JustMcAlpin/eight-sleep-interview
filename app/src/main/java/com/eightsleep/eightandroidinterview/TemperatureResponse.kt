package com.eightsleep.eightandroidinterview

data class TemperatureResponse(
    val phases: List<PhaseResponse>
)

data class PhaseResponse(
    val phase: SleepPhase,
    val temperature: Int
)