package com.csakitheone.froccs

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.databinding.ActivityGlassholderBinding
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder

class GlassholderActivity : AppCompatActivity() {
    lateinit var binding: ActivityGlassholderBinding

    private var selectedColor = Color.Green

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlassholderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.attributes.apply {
            screenBrightness = 1.0f
        }

        hideSystemUI()

        updateGlassholder()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun updateGlassholder() {
        binding.glassholderView.setContent {
            Glassholder(color = selectedColor)
        }
    }

    @Composable
    fun Glassholder(color: Color, minimumSize: Float = 100f) {
        var scale by remember { mutableStateOf(minimumSize + 100f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = rememberTransformableState { zoom: Float, offset: Offset, rotation: Float ->
                    scale *= zoom
                    if (scale < minimumSize) scale = minimumSize
                }),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(scale.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(.2f),
                    text = "Tedd ide a poharad",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Preview
    @Composable
    fun GlassholderPreview() {
        Glassholder(color = Color.Green)
    }

    fun btnColorClick(view: View) {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .initialColor(selectedColor.toArgb())
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .showAlphaSlider(false)
            .showLightnessSlider(false)
            .showColorEdit(true)
            .setColorEditTextColor(Color.Gray.toArgb())
            .density(12)
            .setPositiveButton("Ok") { _, color, _ ->
                selectedColor = Color(color)
                updateGlassholder()
            }
            .setNegativeButton("Mégsem") { _, _ -> }
            .build().show()
    }
}