package com.eightsleep.eightandroidinterview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eightsleep.eightandroidinterview.ui.TemperatureCard
import com.eightsleep.eightandroidinterview.ui.theme.EightAndroidInterviewTheme

class MainActivity : ComponentActivity() {

    private val vm: TempViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EightAndroidInterviewTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TemperatureCard(
                        phase           = vm.selected.value,
                        temps           = vm.temps,          // target values
                        currentTemps    = vm.currentTemps,   // sensed values
                        cardState       = vm.cardState.value,
                        onPhaseSelected = vm::selectPhase,
                        onAdjust        = vm::adjust,
                        onToggleOff     = vm::toggleOff
                    )
                }
            }
        }
    }
}
