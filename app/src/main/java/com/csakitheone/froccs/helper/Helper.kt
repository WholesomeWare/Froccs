package com.csakitheone.froccs.helper

import android.content.Context
import android.util.DisplayMetrics

class Helper {
    companion object {
        fun convertDpToPixel(dp: Float, context: Context): Float {
            return dp * (context.resources.displayMetrics.densityDpi as Float / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun convertPixelsToDp(px: Float, context: Context): Float {
            return px / (context.resources.displayMetrics.densityDpi as Float / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}