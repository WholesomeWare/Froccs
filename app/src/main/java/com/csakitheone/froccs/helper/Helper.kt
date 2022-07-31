package com.csakitheone.froccs.helper

import android.content.Context
import android.util.DisplayMetrics
import com.csakitheone.froccs.data.Prefs
import kotlin.math.roundToInt

class Helper {
    companion object {

        fun Float.roundToPreference(): Float {
            return if (Prefs.preciseSliders) (this * 20).roundToInt() / 2f
            else (this * 10).roundToInt().toFloat()
        }

    }
}