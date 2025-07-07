package com.eightsleep.eightandroidinterview.data

import kotlinx.coroutines.delay

object FakePadService {
    /** Simulate hardware drift (1-step closer to target after 750 ms). */
    suspend fun nudge(current: Int, target: Int): Int {
        delay(750)
        return when {
            current < target -> current + 1
            current > target -> current - 1
            else             -> current
        }
    }
}
