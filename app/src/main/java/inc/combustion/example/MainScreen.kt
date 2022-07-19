/*
 * Project: Combustion Inc. Android Example
 * File: MainScreen.kt
 * Author: https://github.com/miwright2
 *
 * MIT License
 *
 * Copyright (c) 2022. Combustion Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package inc.combustion.example

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import inc.combustion.example.devices.DevicesScreen
import inc.combustion.example.settings.SettingsScreen
import inc.combustion.example.theme.CombustionIncEngineeringTheme
import inc.combustion.example.theme.Combustion_Red

sealed class AppScreen(val route: String) {
    object Devices : AppScreen(route = "Devices")
    object Details : AppScreen(route = "Details")
    object Settings : AppScreen(route = "Settings")
}

class AppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
) {
    var noDevicesReasonString = ""

    @Composable
    fun NavHost(modifier: Modifier) {
        androidx.navigation.compose.NavHost(
            navController,
            startDestination = AppScreen.Devices.route,
            modifier
        ) {
            val appState = this@AppState
            composable(AppScreen.Settings.route) { SettingsScreen(appState) }
            composable(AppScreen.Devices.route) { DevicesScreen(appState) }
            /*
            for (screen in bottomNavigationScreens) {
                composable(screen.route) {
                    when (screen) {
                        is AppScreen.Devices -> DevicesScreen(appState)
                        is AppScreen.Settings -> SettingsScreen(appState)
                    }
                }
            }
             */
        }
    }

    fun onTopBarNavigationClick(): () -> Unit {
        return {
            // do nothing at this point.  maybe add drawer in future.
        }
    }

    fun onTopBarActionClick(): () -> Unit {
        return {
            // navigate to the composable.
            //onNavigate(topNavigationScreen.route)
            onNavigate(AppScreen.Settings.route)
        }
    }

    private fun onNavigate(route: String) {
        navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
) = remember(
    scaffoldState,
    navController,
) {
    AppState(
        scaffoldState = scaffoldState,
        navController = navController,
    )
}

/*
@Composable
fun MainScreenContent(
    appState: AppState,
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings_24),
                            contentDescription = stringResource(id = R.string.settings_screen_title)
                        )
                    }
                }
            )
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
 */

@Composable
fun MainScreenContent(
    appState: AppState,
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings_24),
                            contentDescription = stringResource(id = R.string.settings_screen_title)
                        )
                    }
                }
            )
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

@Composable
fun MainScreen(
    isScanning: State<Boolean>,
    bluetoothIsOn: State<Boolean>
) {
    CombustionIncEngineeringTheme(darkTheme = true) {

        val appState = rememberAppState()

        if(!bluetoothIsOn.value) {
            appState.noDevicesReasonString = "Please Turn On Bluetooth..."
        }
        else if(!isScanning.value) {
            appState.noDevicesReasonString = "Please Turn On Scanning..."
        }
        else {
            appState.noDevicesReasonString = "Searching..."
        }

        MainScreenContent(appState = appState)
    }
}
