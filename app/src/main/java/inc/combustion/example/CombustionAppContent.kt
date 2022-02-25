package inc.combustion.example

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import inc.combustion.example.devices.DevicesScreen
import inc.combustion.example.theme.CombustionIncEngineeringTheme
import inc.combustion.example.theme.Combustion_Red

/**
 * Top-level user interface for the app.  Follows state hoisting guideline.
 *
 * [Guide: State hoisting](https://developer.android.com/jetpack/compose/state#state-hoisting)
 */
@Composable
fun CombustionAppContent(
    appState: CombustionAppState,
    content: @Composable (PaddingValues) -> Unit = { padding ->
        appState.NavHost(Modifier.padding(padding))
    }
) {
    Scaffold (
        scaffoldState = appState.scaffoldState,
        topBar = {
            TopAppBar (
                title = { Text(
                    text = stringResource(id = R.string.app_title),
                    color = MaterialTheme.colors.onSecondary
                ) },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = appState.onTopBarNavigationClick()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = stringResource(R.string.menu_button_description),
                            tint = Combustion_Red
                        )
                    }
                },
                actions = {
                    IconButton(onClick = appState.onTopBarActionClick()) {
                        Icon(
                            tint = MaterialTheme.colors.onBackground,
                            imageVector = appState.topNavigationScreen.getImageVector(),
                            contentDescription = appState.topNavigationScreen.getTitle()
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(backgroundColor = MaterialTheme.colors.background) {
                BottomNavigation(backgroundColor = MaterialTheme.colors.background){
                    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    appState.bottomNavigationScreens.forEach { appScreen ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = appScreen.getImageVector(),
                                    contentDescription = appScreen.getTitle(),
                                    tint = MaterialTheme.colors.onBackground
                                )
                            },
                            onClick = appState.onBottomBarNavigateClick(appScreen),
                            selected = currentDestination?.hierarchy?.any { it.route == appScreen.route } == true
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary,
                    snackbarData = data
                )
            }
        },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    CombustionIncEngineeringTheme {
        val appState = rememberCombustionAppState()
        CombustionAppContent(
            appState = appState,
            content = @Composable {
                DevicesScreen(appState = appState)
            }
        )
    }
}
