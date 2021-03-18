package com.example.jokerway.util

import android.content.Context
import android.util.TypedValue

class DimensionConverter {
    companion object {
        fun dpToPixels(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}