package com.csakitheone.froccs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Prefs
import com.csakitheone.froccs.ui.components.RecipeView
import com.csakitheone.froccs.ui.components.TabFroccs
import com.csakitheone.froccs.ui.theme.FröccsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val DEMO_MODE = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
            val darkTheme = isSystemInDarkTheme()
            val coroutineScope = rememberCoroutineScope()

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            var isMenuOpen by remember { mutableStateOf(false) }
            var isCoasterWarningDialogVisible by remember { mutableStateOf(false) }

            val recipes by remember {
                mutableStateOf(Data.getRecipes().filter { recipe -> recipe.ingredients.size == 2 })
            }

            LaunchedEffect(darkTheme, drawerState.isClosed) {
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars = drawerState.isClosed == darkTheme
            }

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
                DismissibleNavigationDrawer(
                    modifier = Modifier.fillMaxSize(),
                    drawerState = drawerState,
                    drawerContent = {
                        DismissibleDrawerSheet(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            drawerState = drawerState,
                            drawerContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = stringResource(id = R.string.main_tab_recipes),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            recipes.forEach { recipe ->
                                RecipeView(
                                    recipe = recipe,
                                    onClick = {
                                        //TODO: select recipe
                                    }
                                )
                            }
                        }
                    },
                ) {
                    Column {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.app_name)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (drawerState.isClosed) drawerState.open()
                                            else drawerState.close()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (drawerState.isClosed) Icons.Default.Menu
                                        else Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            },
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
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                        TabFroccs()
                        Spacer(modifier = Modifier.systemBarsPadding())
                    }
                }
            }
        }
    }
}