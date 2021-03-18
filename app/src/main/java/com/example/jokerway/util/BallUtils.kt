package com.example.jokerway.util

import android.content.Context
import com.example.jokerway.model.Direction

class BallUtils(context: Context) {
    private var endPadding = DimensionConverter.dpToPixels(end_padding_px, context)
    private var sidePadding = DimensionConverter.dpToPixels(side_padding_px, context)
    private var startPadding = DimensionConverter.dpToPixels(start_padding_px, context)

    fun getActualLocation(ballPosition: IntArray, currentDirection: Direction): IntArray {
        when (currentDirection) {
            Direction.DOWN -> {
                ballPosition[0] += sidePadding
                ballPosition[1] += endPadding
            }
            Direction.UP -> {
                ballPosition[0] += sidePadding
                ballPosition[1] += startPadding
            }
            Direction.LEFT -> {
                ballPosition[0] += startPadding
                ballPosition[1] += sidePadding
            }
            Direction.RIGHT -> {
                ballPosition[0] += endPadding
                ballPosition[1] += sidePadding
            }
            else -> {
            }
        }
        return ballPosition
    }

    companion object {
        private const val end_padding_px = 56f
        private const val side_padding_px = 30f
        private const val start_padding_px = 9f
    }
}