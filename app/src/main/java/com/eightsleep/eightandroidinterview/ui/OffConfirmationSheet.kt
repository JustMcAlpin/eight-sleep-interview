package com.eightsleep.eightandroidinterview.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Stylised confirmation bottom‑sheet.
 *
 * @param tint         Accent colour for the filled “Turn Off” button.
 * @param onConfirm    Called when user presses “Turn Off”.
 * @param onDismiss    Called when user presses “Cancel” or swipes sheet away.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffConfirmationSheet(
    tint      : Color = Color(0xFFFF3B30),
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.medium.copy(topStart = RoundedCornerShape(16.dp).topStart,
            topEnd   = RoundedCornerShape(16.dp).topEnd),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* headline */
            Text(
                text = "Turn off Perfect Temp?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            /* subtitle */
            Text(
                text = "You can turn it back on any time.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            /* action row */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                /** Cancel (outlined pill) */
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0B0B0))
                ) {
                    Text("Cancel", color = Color.Black)
                }

                /** Turn Off (filled pill) */
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tint)
                ) {
                    Text("Turn Off")
                }
            }
        }
    }
}
