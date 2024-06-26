package com.csakitheone.froccs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.csakitheone.froccs.ui.theme.FröccsTheme
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import java.util.Timer
import kotlin.concurrent.timerTask

class CoasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.attributes.screenBrightness = 1.0f
        enableEdgeToEdge()

        setContent {
            CoasterScreen()
        }

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Preview
    @Composable
    fun CoasterScreen() {
        var isColorPickerDialogOpen by remember { mutableStateOf(false) }
        var selectedColor by remember { mutableStateOf(Color.Green) }
        var isAnimating by remember { mutableStateOf(false) }
        var isLocked by remember { mutableStateOf(false) }

        val gradientColors = listOf(
            Color.Red,
            Color.Magenta,
            Color.Blue,
            Color.Cyan,
            Color.Green,
            Color.Yellow,
        )
        var currentColorIndex by remember { mutableIntStateOf(0) }
        val animatedColor by animateColorAsState(
            targetValue = selectedColor,
            animationSpec = tween(2000, easing = LinearEasing),
        )

        LaunchedEffect(Unit) {
            Timer().schedule(timerTask {
                if (isAnimating) {
                    currentColorIndex++
                    if (currentColorIndex >= gradientColors.size) currentColorIndex = 0
                    selectedColor = gradientColors[currentColorIndex]
                }
            }, 0, 2000)
        }

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
                        color = if (isAnimating) animatedColor else selectedColor,
                        isSizeLocked = isLocked,
                    )

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .alpha(.3f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnimatedVisibility(visible = !isLocked && !isAnimating) {
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
                        }
                        AnimatedVisibility(visible = !isLocked) {
                            Switch(
                                modifier = Modifier.padding(8.dp),
                                checked = isAnimating,
                                onCheckedChange = { isAnimating = it },
                            )
                        }
                        IconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                isLocked = !isLocked
                            },
                        ) {
                            Icon(
                                imageVector = if (isLocked) Icons.Default.Lock
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