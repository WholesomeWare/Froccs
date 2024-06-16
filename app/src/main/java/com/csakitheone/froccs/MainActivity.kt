package com.csakitheone.froccs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Prefs
import com.csakitheone.froccs.ui.tabs.RecipesScreen
import com.csakitheone.froccs.ui.components.SettingsScreen
import com.csakitheone.froccs.ui.tabs.TabFroccs
import com.csakitheone.froccs.ui.theme.FröccsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val DEMO_MODE = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Prefs.init(this)
        Data.load(this)

        setContent {
            MainScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun MainScreen() {
        FröccsTheme {
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(pageCount = { 2 })
            var isMenuOpen by remember { mutableStateOf(false) }
            var isCoasterWarningDialogVisible by remember { mutableStateOf(false) }

            if (isCoasterWarningDialogVisible) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.coaster)) },
                    text = { Text(text = stringResource(id = R.string.coaster_warning)) },
                    onDismissRequest = { isCoasterWarningDialogVisible = false },
                    dismissButton = {
                        TextButton(
                            onClick = { isCoasterWarningDialogVisible = false }
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                startActivity(Intent(this, CoasterActivity::class.java))
                                isCoasterWarningDialogVisible = false
                            }
                        ) {
                            Text(text = "Ok")
                        }
                    },
                )
            }

            Surface(color = MaterialTheme.colorScheme.background) {
                Column {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.app_name)) },
                        actions = {
                            IconButton(onClick = { isMenuOpen = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                                DropdownMenu(
                                    expanded = isMenuOpen,
                                    onDismissRequest = { isMenuOpen = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(id = R.string.coaster)) },
                                        onClick = {
                                            isCoasterWarningDialogVisible = true
                                            isMenuOpen = false
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )

                    HorizontalPager(
                        modifier = Modifier.weight(1f),
                        state = pagerState,
                    ) { page ->
                        Box(
                            modifier = Modifier.fillMaxSize(1f)
                        ) {
                            when (page) {
                                0 -> TabFroccs()
                                1 -> RecipesScreen()
                            }
                        }
                    }

                    NavigationBar {
                        NavigationBarItem(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bottle_wine),
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(id = R.string.main_tab_mixing)) }
                        )
                        NavigationBarItem(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_grapes),
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(id = R.string.main_tab_recipes)) }
                        )
                    }
                }
            }
        }
    }
}