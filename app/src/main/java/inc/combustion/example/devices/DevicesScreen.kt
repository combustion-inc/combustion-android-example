/*
 * Project: Combustion Inc. Android Example
 * File: DevicesScreen.kt
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
package inc.combustion.example.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.example.components.SingleSelectDialog
import inc.combustion.example.AppState
import inc.combustion.example.components.*
import inc.combustion.framework.service.*

/**
 * Data object for DeviceScreen state.
 *
 * @property probes Observable map of serial number -> ProbeState.
 * @property onUnitsClick Lambda for handling click on units card button.
 * @property onBluetoothClick Lambda for handling click on Bluetooth card button.
 *
 * @see ProbeState
 * @see DevicesScreen
 */
data class DevicesScreenState(
    val probes: SnapshotStateMap<String, ProbeState>,
    val onUnitsClick: () -> Unit,
    val onBluetoothClick: (ProbeState) -> Unit,
    val onSetProbeColorClick: (String, ProbeColor) -> Unit,
    val onSetProbeIDClick: (String, ProbeID) -> Unit,
    val onCardClick: (String) -> Unit
)

@Composable
fun DevicesScreen(
    appState: AppState
) {
    val unitsConversion: (Double) -> Double = { celsius ->
        appState.convertTemperature(celsius)
    }

    val viewModel : DevicesViewModel = viewModel(
        factory = DevicesViewModel.Factory(DeviceManager.instance, unitsConversion)
    )

    val screenState = DevicesScreenState(
        probes = remember { viewModel.probes },
        onUnitsClick = {
            appState.cycleUnits()
        },
        onBluetoothClick = { device ->
            viewModel.toggleConnection(device)
        },
        onSetProbeColorClick = { serialNumber, color ->
            viewModel.setProbeColor(serialNumber, color)
        },
        onSetProbeIDClick = { serialNumber, id ->
            viewModel.setProbeID(serialNumber, id)
        },
        onCardClick = { serialNumber ->
            appState.navigateToDetails(serialNumber)
        }
    )

    DevicesContent(
        appState,
        screenState
    )
}

@Composable
fun DevicesContent(
    appState: AppState,
    screenState: DevicesScreenState
) {
    AppScaffold(
        title = "Combustion Inc.",
        navigationIcon = { CombustionIconButton() },
        actionIcons = {
            SettingsIconButton(onClick = { appState.navigateToSettings() })
        },
        appState = appState
    ) {
        val list = screenState.probes.values.toMutableStateList()

        if (list.size == 0) {
            AppProgressIndicator(reason = appState.noDevicesReasonString)
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
            ) {
                items(list) { item ->
                    DeviceSummaryCard(
                        appState = appState,
                        probeState = item,
                        onCardClick = { screenState.onCardClick(item.serialNumber) },
                        onConnectionClick = { screenState.onBluetoothClick(item) },
                        onUnitsClick = { screenState.onUnitsClick() }
                    )
                }
            }
        }
    }
}
