package com.csakitheone.froccs.ui.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.CoasterActivity
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Prefs

@Preview
@Composable
fun SettingsScreen(activity: Activity? = null) {
    var preciseSliders: Boolean by remember { mutableStateOf(Prefs.preciseSliders) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        GlassholderButton()

        //SupportDeveloperLayout(activity)

        LabeledSwitch(
            label = stringResource(id = R.string.precise_sliders),
            checked = preciseSliders,
            onCheckedChange = {
                Prefs.preciseSliders = it
                preciseSliders = Prefs.preciseSliders
            }
        )
    }
}

@Composable
fun GlassholderButton() {
    val context = LocalContext.current

    var isDialogVisible by remember { mutableStateOf(false) }

    Button(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            isDialogVisible = true

        }
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_glass_wine), contentDescription = null)
        Text(text = stringResource(id = R.string.coaster))
    }

    if (isDialogVisible) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.coaster)) },
            text = { Text(text = stringResource(id = R.string.coaster_warning)) },
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        context.startActivity(Intent(context, CoasterActivity::class.java))
                        isDialogVisible = false
                    }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }
}

@Composable
fun LabeledSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onCheckedChange(!checked)
            }
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = label,
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}