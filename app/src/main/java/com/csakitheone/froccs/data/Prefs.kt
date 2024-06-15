package com.csakitheone.froccs.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs {
    companion object {

        private var prefs: SharedPreferences? = null

        fun init(context: Context) {
            //prefs = PreferenceManager.getDefaultSharedPreferences(context)
        }

        @Deprecated("Rewrite needed")
        var preciseSliders: Boolean
            get() = false//prefs?.getBoolean("precise_sliders", false) ?: false
            set(value) { /*prefs?.edit { putBoolean("precise_sliders", value); commit() }*/ }

    }
}