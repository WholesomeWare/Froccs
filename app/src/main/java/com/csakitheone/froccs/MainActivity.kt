package com.csakitheone.froccs

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.IO
import com.csakitheone.froccs.data.Prefs
import com.csakitheone.froccs.ui.components.MixingScreen
import com.csakitheone.froccs.ui.components.RecipesScreen
import com.csakitheone.froccs.ui.components.SettingsScreen
import com.csakitheone.froccs.ui.theme.FröccsTheme
import com.google.android.gms.ads.*

class MainActivity : ComponentActivity() {
    val DEMO_MODE = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Prefs.init(this)
        Data.loadUserData(this)

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("FB727C3E2E7E37FB1BCB7C55C84A9993"))
                .build()
        )
        MobileAds.initialize(this)

        setContent {
            MainScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @Preview(showBackground = true)
    @Composable
    fun MainScreen() {
        var selectedTab by remember { mutableStateOf(0) }
        
        FröccsTheme {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    AnimatedContent(targetState = selectedTab) { tab ->
                        when (tab) {
                            0 -> MixingScreen()
                            1 -> RecipesScreen()
                            2 -> SettingsScreen(this@MainActivity)
                        }
                    }
                }

                if (!LocalInspectionMode.current && !DEMO_MODE) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = {
                            AdView(it).apply {
                                setAdSize(AdSize.BANNER)
                                adUnitId = "ca-app-pub-5995992409743558/6068638221"
                                loadAd(AdRequest.Builder().build())
                            }
                        }
                    )
                }
                
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.ic_bottle_wine), contentDescription = null)
                        },
                        label = { Text(text = stringResource(id = R.string.main_tab_mixing)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.ic_grapes), contentDescription = null)
                        },
                        label = { Text(text = stringResource(id = R.string.main_tab_recipes)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        },
                        label = { Text(text = stringResource(id = R.string.main_tab_settings)) }
                    )
                }
            }
        }
    }
}