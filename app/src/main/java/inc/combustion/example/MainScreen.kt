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

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import inc.combustion.example.details.DetailsScreen
import inc.combustion.example.devices.DevicesScreen
import inc.combustion.example.settings.SettingsScreen
import inc.combustion.example.theme.CombustionIncEngineeringTheme

sealed class AppScreen(val route: String, val title: String) {
    object Devices : AppScreen(route = "devices", title = "Devices")
    object Details : AppScreen(route = "details", title = "Details")
    object Settings : AppScreen(route = "settings", title = "Settings")
}

class AppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    val isScanning: State<Boolean>,
    val bluetoothIsOn: State<Boolean>,
    val onShareTextData: (String, String) -> Unit,
    val showMeasurements: MutableState<Boolean> = mutableStateOf(true),
    val showPlot: MutableState<Boolean> = mutableStateOf(true),
    val showDetails: MutableState<Boolean> = mutableStateOf(false),
    val showInstantRead: MutableState<Boolean> = mutableStateOf(true),
    val showTemperatures: MutableState<Boolean> = mutableStateOf(true),
    val showPrediction: MutableState<Boolean> = mutableStateOf(true)
) {
    val noDevicesReasonString: String
        get() = if(!bluetoothIsOn.value) {
                    "Please Turn On Bluetooth..."
                }
                else if(!isScanning.value) {
                    "Please Turn On Scanning..."
                }
                else {
                    "Searching..."
                }

    @Composable
    fun NavHost(modifier: Modifier) {
        androidx.navigation.compose.NavHost(
            navController,
            startDestination = AppScreen.Devices.route,
            modifier
        ) {
            val appState = this@AppState
            composable(AppScreen.Settings.route) {
                SettingsScreen(appState)
            }
            composable(AppScreen.Devices.route) {
                DevicesScreen(appState)
            }
            composable(
                route = AppScreen.Details.route + "/{serialNumber}",
                arguments = listOf(navArgument(name = "serialNumber") {
                    type = NavType.StringType
                })
            ) { entry ->
                val serialNumber = entry.arguments?.getString("serialNumber")
                DetailsScreen(appState = appState, serialNumber = serialNumber)
            }
        }
    }

    fun navigateToDetails(serialNumber: String) {
        onNavigate(AppScreen.Details.route + "/$serialNumber")
    }

    fun navigateToSettings() {
        onNavigate(AppScreen.Settings.route)
    }

    fun navigateBack() {
        navController.navigateUp()
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
    isScanning: State<Boolean>,
    bluetoothIsOn: State<Boolean>,
    onShareTextData: (String, String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
) = remember(
    scaffoldState,
    navController,
    isScanning,
    onShareTextData,
    bluetoothIsOn
) {
    AppState(
        scaffoldState = scaffoldState,
        navController = navController,
        onShareTextData = onShareTextData,
        isScanning = isScanning,
        bluetoothIsOn = bluetoothIsOn
    )
}

@Composable
fun MainScreen(
    isScanning: State<Boolean>,
    bluetoothIsOn: State<Boolean>,
    onShareTextData: (String, String) -> Unit
) {
    CombustionIncEngineeringTheme(darkTheme = true) {

        val appState = rememberAppState(isScanning, bluetoothIsOn, onShareTextData)

        appState.NavHost(modifier = Modifier)
    }
}
