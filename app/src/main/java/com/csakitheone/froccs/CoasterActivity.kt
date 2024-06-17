package com.csakitheone.froccs

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.csakitheone.froccs.ui.theme.FröccsTheme
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType

class CoasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.attributes.screenBrightness = 1.0f
        enableEdgeToEdge()

        setContent {
            CoasterScreen()
        }

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Preview
    @Composable
    fun CoasterScreen() {
        var isColorPickerDialogOpen by remember { mutableStateOf(false) }
        var selectedColor by remember { mutableStateOf(Color.Green) }
        var isSizeLocked by remember { mutableStateOf(false) }

        FröccsTheme {
            ColorPickerDialog(
                show = isColorPickerDialogOpen,
                type = ColorPickerType.Circle(
                    showBrightnessBar = false,
                    showAlphaBar = false,
                    lightCenter = true,
                ),
                onDismissRequest = { isColorPickerDialogOpen = false },
                onPickedColor = {
                    selectedColor = it
                    isColorPickerDialogOpen = false
                },
            )

            Surface(
                color = Color.Black,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Coaster(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        color = selectedColor,
                        isSizeLocked = isSizeLocked,
                    )

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .alpha(.3f),
                    ) {
                        IconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                isColorPickerDialogOpen = true
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_color_lens),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        IconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                isSizeLocked = !isSizeLocked
                            },
                        ) {
                            Icon(
                                imageVector = if (isSizeLocked) Icons.Default.Lock
                                else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Coaster(
        modifier: Modifier,
        color: Color,
        isSizeLocked: Boolean = false,
        minimumSize: Float = 80f,
    ) {
        var scale by remember { mutableStateOf(minimumSize + 100f) }

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = rememberTransformableState { zoom: Float, offset: Offset, rotation: Float ->
                        if (!isSizeLocked) {
                            scale *= zoom
                            if (scale < minimumSize) scale = minimumSize
                        }
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
                        text = stringResource(id = R.string.place_your_glass_here),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}