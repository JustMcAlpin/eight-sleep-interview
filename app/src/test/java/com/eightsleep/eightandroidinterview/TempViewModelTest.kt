package com.eightsleep.eightandroidinterview

import com.eightsleep.eightandroidinterview.ui.CardState
import com.eightsleep.eightandroidinterview.ui.TemperaturePhase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TempViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun adjust_whenCooling_reachesTargetAndGoesIdle() = runTest {
        val vm = TempViewModel()

        vm.adjust(-1)                                // -2 ➜ -3  (COOLING)
        assertEquals(CardState.COOLING, vm.cardState.value)

        advanceTimeBy(800)                            // skip FakePadService delay
        advanceUntilIdle()                            // finish all coroutines
        assertEquals(CardState.IDLE, vm.cardState.value)
        assertEquals(-3, vm.currentTemps[TemperaturePhase.BEDTIME])
    }

    @Test
    fun adjust_whenWarming_reachesTargetAndGoesIdle() = runTest {
        val vm = TempViewModel()
        vm.adjust(+1)                                // -2 ➜ -1  (WARMING)

        advanceTimeBy(800)
        advanceUntilIdle()
        assertEquals(CardState.IDLE, vm.cardState.value)
        assertEquals(-1, vm.currentTemps[TemperaturePhase.BEDTIME])
    }

    @Test
    fun toggleOff_turnsOffAndBackOn() {
        val vm = TempViewModel()

        vm.toggleOff()                               // OFF
        assertEquals(CardState.OFF, vm.cardState.value)

        vm.toggleOff()                               // back ON (IDLE)
        assertEquals(CardState.IDLE, vm.cardState.value)
    }

    @Test
    fun adjust_clampsBeyondRange() {
        val vm = TempViewModel()
        repeat(15) { vm.adjust(-1) }                 // try to push below -10
        assertTrue(vm.temps[TemperaturePhase.BEDTIME]!! >= -10)
    }

    @Test
    fun adjust_whileOff_noOp() = runTest {
        val vm = TempViewModel()
        vm.toggleOff()                               // OFF
        vm.adjust(+1)

        assertEquals(CardState.OFF, vm.cardState.value)
        assertEquals(-2, vm.temps[TemperaturePhase.BEDTIME]) // unchanged
    }

    @Test
    fun phaseSwitch_isolatedTemperatures() {
        val vm = TempViewModel()

        vm.selectPhase(TemperaturePhase.DAWN)
        vm.adjust(-2)                                // 4 ➜ 2

        assertEquals(2, vm.temps[TemperaturePhase.DAWN])
        assertEquals(-2, vm.temps[TemperaturePhase.BEDTIME]) // original value untouched
    }

    @Test
    fun adjust_cancelsPreviousDriftJob() = runTest {
        val vm = TempViewModel()

        vm.adjust(+1)                                // start warming to -1
        vm.adjust(-2)                                // immediately cool to -3 (new job)

        advanceTimeBy(800)
        advanceUntilIdle()

        assertEquals(CardState.IDLE, vm.cardState.value)
        assertEquals(-3, vm.currentTemps[TemperaturePhase.BEDTIME]) // final target = -3
    }
}
