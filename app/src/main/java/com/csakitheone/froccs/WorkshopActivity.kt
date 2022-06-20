package com.csakitheone.froccs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.csakitheone.froccs.databinding.ActivityWorkshopBinding
import com.csakitheone.froccs.helper.Workshop

class WorkshopActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkshopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Workshop.getAllStrings(Workshop.WORKSHOP_CATEGORY_RECIPE) {
            binding.workshopText.text = it.reversed().joinToString("\n\n")
        }
    }
}