package com.csakitheone.froccs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Prefs
import com.csakitheone.froccs.ui.components.RecipeView
import com.csakitheone.froccs.ui.components.TabFroccs
import com.csakitheone.froccs.ui.components.VineBottle
import com.csakitheone.froccs.ui.theme.FröccsTheme
import com.csakitheone.wholesomeware_brand.WholesomeWare
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

            var glassSize by remember { mutableIntStateOf(2) }
            var ingredientsRatio by remember { mutableFloatStateOf(.5f) }
            val amountVine by remember(glassSize, ingredientsRatio) {
                mutableFloatStateOf(
                    (ingredientsRatio * glassSize * 2).roundToInt() / 2f
                )
            }
            val amountSoda by remember(glassSize, ingredientsRatio) {
                mutableFloatStateOf(
                    glassSize - ((ingredientsRatio * glassSize * 2).roundToInt() / 2f)
                )
            }
            val recipe by remember(amountSoda, amountVine) {
                mutableStateOf(
                    Data.getRecipes().firstOrNull { recipe ->
                        recipe.ingredients.firstOrNull { it.name == getString(R.string.ingredient_soda) }?.amount == amountSoda &&
                                recipe.ingredients.firstOrNull { it.name == getString(R.string.ingredient_vine) }?.amount == amountVine
                    }
                )
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
                            recipes.forEach { r ->
                                RecipeView(
                                    recipe = r,
                                    onClick = {
                                        glassSize = r.getSize().toInt()
                                        ingredientsRatio = r.getRatio()
                                        coroutineScope.launch {
                                            drawerState.close()
                                        }
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
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(id = R.string.more_apps)) },
                                            leadingIcon = {
                                                Icon(
                                                    painterResource(id = com.csakitheone.wholesomeware_brand.R.drawable.ic_wholesomeware),
                                                    contentDescription = null,
                                                )
                                            },
                                            onClick = {
                                                WholesomeWare.openPlayStore(this@MainActivity)
                                                isMenuOpen = false
                                            },
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

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                            ) {
                                Data.getGlassSizes()
                                    .filter { it != 0 }
                                    .forEach { size ->
                                        FilterChip(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .graphicsLayer {
                                                    scaleX = if (glassSize == size) 1.2f else 1f
                                                    scaleY = if (glassSize == size) 1.2f else 1f
                                                },
                                            label = { Text(text = "${size}dl") },
                                            selected = glassSize == size,
                                            onClick = {
                                                glassSize = size
                                            },
                                        )
                                    }
                            }

                            VineBottle(
                                modifier = Modifier.size(250.dp),
                                fullness = glassSize / 10f,
                            )

                            AnimatedContent(targetState = recipe) {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = it?.name ?: "",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                )
                            }

                            Column(
                                modifier = Modifier.padding(32.dp),
                            ) {
                                Box(
                                    modifier = Modifier.offset(y = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Data.getRecipes()
                                        .filter { it.getSize() == glassSize.toFloat() }
                                        .forEach { recipe ->
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 2.dp)
                                                    .fillMaxWidth(),
                                            ) {
                                                Spacer(
                                                    modifier = Modifier.fillMaxWidth(recipe.getRatio())
                                                )
                                                Surface(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .offset(x = (-4).dp)
                                                        .clip(CircleShape),
                                                    color = MaterialTheme.colorScheme.primary
                                                ) {}
                                            }
                                        }
                                }

                                Slider(
                                    value = ingredientsRatio,
                                    onValueChange = { ingredientsRatio = it },
                                    steps = glassSize * 2 - 1,
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "${amountVine}dl ${stringResource(id = R.string.ingredient_vine)}"
                                            .replace(".0", ""),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = "${amountSoda}dl ${stringResource(id = R.string.ingredient_soda)}"
                                            .replace(".0", ""),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}