/*
 * Project: Combustion Inc. Android Example
 * File: DetailsScreen.kt
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

package inc.combustion.example.details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.example.components.SingleSelectDialog
import inc.combustion.example.AppState
import inc.combustion.example.components.*
import inc.combustion.framework.service.DeviceManager
import inc.combustion.framework.service.LoggedProbeDataPoint
import inc.combustion.framework.service.ProbeColor
import inc.combustion.framework.service.ProbeID
import java.util.*

data class DetailsScreenState(
    val serialNumber: String,
    val probeState: ProbeState,
    val probeData: SnapshotStateList<LoggedProbeDataPoint> = mutableStateListOf(),
    val probeDataStartTimestamp: MutableState<Date>,
    val measurementCardIsExpanded: MutableState<Boolean>,
    val plotCardIsExpanded: MutableState<Boolean>,
    val detailsCardIsExpanded: MutableState<Boolean>,
    val instantReadCardIsExpanded: MutableState<Boolean>,
    val temperaturesCardIsExpanded: MutableState<Boolean>,
    val predictionsCardIsExpanded: MutableState<Boolean>,
    val onConnectClick: () -> Unit,
    val onSetProbeColorClick: (ProbeColor) -> Unit,
    val onSetProbeIDClick: (ProbeID) -> Unit,
    val onShareClick: () -> Unit,
)

@Composable
fun DetailsScreen(
    appState: AppState,
    serialNumber: String?
) {
    val viewModel : DetailsViewModel = viewModel(
        factory = DetailsViewModel.Factory(
            DeviceManager.instance,
            serialNumber ?: "?"
        )
    )

    val screenState = DetailsScreenState(
        serialNumber = viewModel.serialNumber,
        probeState =  viewModel.probe,
        probeData = viewModel.probeData,
        probeDataStartTimestamp = viewModel.probeDataStartTimestamp,
        measurementCardIsExpanded = appState.showMeasurements,
        plotCardIsExpanded = appState.showPlot,
        detailsCardIsExpanded = appState.showDetails,
        instantReadCardIsExpanded = appState.showInstantRead,
        temperaturesCardIsExpanded = appState.showTemperatures,
        predictionsCardIsExpanded = appState.showPrediction,
        onConnectClick =  { viewModel.toggleConnection() },
        onSetProbeColorClick = { color -> viewModel.setProbeColor(color) },
        onSetProbeIDClick = { id -> viewModel.setProbeID(id) },
        onShareClick = {
            val (fileName, fileData) = viewModel.getShareData()
            appState.onShareTextData(fileName, fileData)
        }
    )

    DetailsContent(
        appState = appState,
        screenState = screenState
    )
}

@Composable
fun DetailsContent(
    appState: AppState,
    screenState: DetailsScreenState
) {
    var showProbeColorDialog by remember { mutableStateOf(false) }
    var showProbeIDDialog by remember { mutableStateOf(false) }
    var shareIsEnabled = screenState.probeData.size > 0

    if (showProbeColorDialog) {
        SingleSelectDialog(title = "Select Probe Color",
            optionsList = ProbeColor.stringValues(),
            defaultSelected = 0,
            submitButtonText = "OK",
            onSubmitButtonClick = {
                val selectedColor = ProbeColor.fromRaw(it.toUInt())
                screenState.onSetProbeColorClick(selectedColor)
                showProbeColorDialog = false
            },
            onDismissRequest = { showProbeColorDialog = false })
    }

    if (showProbeIDDialog) {
        SingleSelectDialog(title = "Select Probe ID",
            optionsList = ProbeID.stringValues(),
            defaultSelected = 0,
            submitButtonText = "OK",
            onSubmitButtonClick = {
                val selectedID = ProbeID.fromRaw(it.toUInt())
                screenState.onSetProbeIDClick(selectedID)
                showProbeIDDialog = false
            },
            onDismissRequest = { showProbeIDDialog = false })
    }

    AppScaffold(
        title = screenState.serialNumber,
        navigationIcon = {
            BackIconButton(onClick = { appState.navigateBack() })
        },
        actionIcons = {
            ShareIconButton(
                enable = shareIsEnabled,
                onClick = { screenState.onShareClick() }
            )
            ConnectionStateButton(
                probeState = screenState.probeState,
                onClick = screenState.onConnectClick
            )
        },
        appState = appState
    ) {
        if (!appState.isScanning.value || !appState.bluetoothIsOn.value) {
            AppProgressIndicator(
                reason = appState.noDevicesReasonString
            )
        } else {
            LazyColumn {
                item {
                    InstantReadCard(
                        probeState = screenState.probeState,
                        cardIsExpanded = screenState.instantReadCardIsExpanded,
                    )
                }
                item {
                    TemperaturesCard(
                        probeState = screenState.probeState,
                        cardIsExpanded = screenState.temperaturesCardIsExpanded,
                    )
                }
                item {
                    PredictionsCard(
                        probeState = screenState.probeState,
                        cardIsExpanded = screenState.predictionsCardIsExpanded,
                    )
                }
                item {
                    PlotCard(
                        plotData = screenState.probeData,
                        probeState = screenState.probeState,
                        cardIsExpanded = screenState.plotCardIsExpanded,
                        plotDataStartTimestamp = screenState.probeDataStartTimestamp
                    )
                }
                item {
                    MeasurementsCard(
                        probeState = screenState.probeState,
                        cardIsExpanded = screenState.measurementCardIsExpanded,
                    )
                }
                item {
                    DetailsCard(
                        probeState = screenState.probeState,
                        cardIsExpanded = screenState.detailsCardIsExpanded,
                        onSetProbeColorClick = { showProbeColorDialog = true },
                        onSetProbeIDClick = { showProbeIDDialog = true },
                    )
                }
            }
        }
    }
}