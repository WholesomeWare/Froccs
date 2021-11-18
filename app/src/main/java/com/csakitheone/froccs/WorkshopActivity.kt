package com.csakitheone.froccs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.csakitheone.froccs.helper.Workshop
import kotlinx.android.synthetic.main.activity_workshop.*

class WorkshopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop)

        Workshop.getAllStrings(Workshop.WORKSHOP_CATEGORY_RECIPE) {
            workshopText.text = it.reversed().joinToString("\n\n")
        }
    }
}