package com.csakitheone.froccs

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csakitheone.froccs.ui.theme.FroccsTheme
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs
import kotlin.math.roundToInt

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var gameState by remember { mutableStateOf(STATE_MENU) }
            var gameDifficulty by remember { mutableStateOf(DIFFICULTY_EASY) }

            FroccsTheme {
                when (gameState) {
                    STATE_MENU -> GameMenu(onGameStart = {
                        gameState = STATE_PLAYING
                    })
                    STATE_PLAYING -> GameCards()
                    else -> Text(text = "🍷")
                }

            }
        }
    }

    companion object {
        const val STATE_MENU = "menu"
        const val STATE_PLAYING = "playing"

        const val DIFFICULTY_EASY = "easy"
        const val DIFFICULTY_MEDIUM = "medium"
        const val DIFFICULTY_HARD = "hard"
    }
}

@Preview(showBackground = true)
@Composable
fun GameMenu(onGameStart: (difficulty: String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Fröccs app ivós játék",
            textAlign = TextAlign.Center,
            fontSize = 32.sp
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Találd ki a receptet!",
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { onGameStart(GameActivity.DIFFICULTY_EASY) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Könnyű")
                Text(text = "Csak az alap fröccs receptek")
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { onGameStart(GameActivity.DIFFICULTY_MEDIUM) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Közepes")
                Text(text = "Ismertebb ital receptek")
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { onGameStart(GameActivity.DIFFICULTY_HARD) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Nehéz")
                Text(text = "Van itt minden")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GameCards() {
    val context = LocalContext.current
    var bias by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color(if (bias) 0x2000FF00 else 0x20FF0000))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            DragCard(
                modifier = Modifier.fillMaxSize(),
                onBiasChanged = {
                    bias = it
                },
                onConfirmed = {
                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Feladat szövege")
                    Text(text = "Húzd jobbra a kártyát ha sikerült teljesíteni. Balra ha nem.")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DragCard(
    modifier: Modifier = Modifier,
    onBiasChanged: (isRight: Boolean) -> Unit = {},
    onConfirmed: (isRight: Boolean) -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    var vibrator: Vibrator? by remember { mutableStateOf(null) }
    var jumpbackTimer by remember { mutableStateOf(Timer()) }
    var offsetX by remember { mutableStateOf(0f) }
    var bias by remember { mutableStateOf(false) }
    var inConfirmDistance by remember { mutableStateOf(false) }

    vibrator = LocalContext.current.getSystemService(Vibrator::class.java)

    Card(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                    if (offsetX > 0 && !bias) bias = true
                    else if (offsetX < 0 && bias) bias = false
                    onBiasChanged(bias)
                    if (abs(offsetX) > 600.dp.value && !inConfirmDistance) {
                        inConfirmDistance = true
                        vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else if (abs(offsetX) < 300.dp.value && inConfirmDistance) {
                        inConfirmDistance = false
                        vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                },
                onDragStarted = {
                    jumpbackTimer.cancel()
                },
                onDragStopped = {
                    if (inConfirmDistance) {
                        onConfirmed(bias)
                        vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    inConfirmDistance = false
                    jumpbackTimer = Timer()
                    jumpbackTimer.schedule(timerTask {
                        if (abs(offsetX) > 0) offsetX /= 2
                    }, 0L, 1000 / 60)
                }
            ),
        onClick = { /*TODO*/ }
    ) {
        content()
    }
}