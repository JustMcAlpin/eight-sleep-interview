package com.eightsleep.eightandroidinterview.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**  Helvetica everywhere, Oswald only for the big center number  */
val AppTypography = Typography(
    /* small labels (“Now”, “Night”, etc.) */
    labelSmall  = TextStyle(fontFamily = Helvetica, fontSize = 12.sp),
    labelMedium = TextStyle(fontFamily = Helvetica, fontSize = 14.sp),

    /* top-row mini numbers (-2, +4) */
    bodySmall   = TextStyle(fontFamily = Helvetica, fontSize = 14.sp),

    /* big centre value / “OFF” */
    displayMedium = TextStyle(
        fontFamily = Oswald,
        fontSize   = 56.sp,
        fontWeight = FontWeight.Normal
    )
    // add more roles later if you need them
)
