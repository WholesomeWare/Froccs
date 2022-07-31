package com.csakitheone.froccs.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class Prefs {
    companion object {

        private var prefs: SharedPreferences? = null

        fun init(context: Context) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
        }

        var preciseSliders: Boolean
            get() = prefs?.getBoolean("precise_sliders", false) ?: false
            set(value) { prefs?.edit { putBoolean("precise_sliders", value); commit() } }

    }
}