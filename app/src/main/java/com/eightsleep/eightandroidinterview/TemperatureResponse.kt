package com.eightsleep.eightandroidinterview

/**
 * Response model from mocked service
 * @param phases list of sleep phases with their respective temperatures
 */
data class TemperatureResponse(
    val phases: List<PhaseResponse>
)

/**
 * Represents a temperature setting for a specific sleep phase.
 * @param phase The [SleepPhase] (BEDTIME, NIGHT, DAWN).
 * @param temperature Integer representing the temperature setting, ranging from -10(coldest)..0(neutral)..10(hottest).
 */
data class PhaseResponse(
    val phase: SleepPhase,
    val temperature: Int
)