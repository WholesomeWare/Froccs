package com.csakitheone.froccs

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.ui.theme.FröccsTheme
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder

class CoasterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.attributes.screenBrightness = 1.0f

        setContent {
            CoasterScreen()
        }

        hideSystemUI()
    }

    @Preview
    @Composable
    fun CoasterScreen() {
        var selectedColor by remember { mutableStateOf(Color.Green) }

        FröccsTheme {
            Column(
                modifier = Modifier.background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Coaster(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = selectedColor
                )

                IconButton(
                    onClick = {
                        ColorPickerDialogBuilder
                            .with(this@CoasterActivity)
                            .initialColor(selectedColor.toArgb())
                            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                            .showAlphaSlider(false)
                            .showLightnessSlider(false)
                            .showColorEdit(true)
                            .setColorEditTextColor(Color.Gray.toArgb())
                            .density(12)
                            .setPositiveButton("Ok") { _, color, _ ->
                                selectedColor = Color(color)
                            }
                            .build().show()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_color_lens),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = .2f)
                    )
                }
            }
        }
    }

    private fun hideSystemUI() {
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.insetsController?.hide(WindowInsets.Type.systemBars())
    }

    @Composable
    fun Coaster(modifier: Modifier, color: Color, minimumSize: Float = 80f) {
        var scale by remember { mutableStateOf(minimumSize + 100f) }

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
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
                        text = stringResource(id = R.string.put_your_glass_here),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}