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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.example.R
import inc.combustion.engineering.ui.components.SingleSelectDialog
import inc.combustion.example.AppScaffold
import inc.combustion.example.AppState
import inc.combustion.example.theme.Combustion_Red
import inc.combustion.framework.service.*

/**
 * State data object for a probe.  Binds the state between the DeviceScreen's ViewModel
 * and Composable functions.  All of the properties in this class are observable using Kotlin State.
 *
 * @property serialNumber serial number.
 * @property macAddress Bluetooth MAC address.
 * @property firmwareVersion firmware version.
 * @property hardwareRevision hardware revision.
 * @property rssi current received signal strength.
 * @property temperaturesCelsius current temperature values in Celsius.
 * @property connectionState current connect state.
 * @property units user's temperature units preference.
 * @property uploadStatus user friendly status string of the upload
 * @property recordsDownloaded number of records stored by the service
 * @property recordRange the record range on the porbe
 * @property color the probe's color setting.
 * @property id the probes ID setting.
 * @property instantRead the probe's Instant Read value.
 */
data class ProbeState(
    val serialNumber: String,
    var macAddress: MutableState<String> = mutableStateOf("TBD"),
    var firmwareVersion: MutableState<String?> = mutableStateOf(null),
    var hardwareRevision: MutableState<String?> = mutableStateOf(null),
    var rssi: MutableState<Int> = mutableStateOf(0),
    var temperaturesCelsius: SnapshotStateList<Double> = mutableStateListOf(
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0
    ),
    var connectionState: MutableState<ConnectionState> = mutableStateOf(ConnectionState.OUT_OF_RANGE),
    var units: MutableState<Units> = mutableStateOf(Units.FAHRENHEIT),
    var uploadStatus: MutableState<String> = mutableStateOf(""),
    var recordsDownloaded: MutableState<Int> = mutableStateOf(0),
    var recordRange: MutableState<String> = mutableStateOf(""),
    var color: MutableState<String> = mutableStateOf(""),
    var id: MutableState<String> = mutableStateOf(""),
    var batteryStatus: MutableState<String> = mutableStateOf(""),
    var instantRead: MutableState<String> = mutableStateOf("")
) {
    enum class Units(val string: String) {
        FAHRENHEIT("Fahrenheit"),
        CELSIUS("Celsius")
    }

    enum class ConnectionState(val string: String) {
        OUT_OF_RANGE("Out of Range"),
        ADVERTISING_CONNECTABLE("Advertising Connectable"),
        ADVERTISING_NOT_CONNECTABLE("Advertising Not Connectable"),
        CONNECTING("Connecting"),
        CONNECTED("Connected"),
        DISCONNECTING("Disconnecting"),
        DISCONNECTED("Disconnected");

        companion object {
            fun fromDeviceConnectionState(state: DeviceConnectionState) : ConnectionState {
                return when(state) {
                    DeviceConnectionState.OUT_OF_RANGE -> OUT_OF_RANGE
                    DeviceConnectionState.ADVERTISING_CONNECTABLE -> ADVERTISING_CONNECTABLE
                    DeviceConnectionState.ADVERTISING_NOT_CONNECTABLE -> ADVERTISING_NOT_CONNECTABLE
                    DeviceConnectionState.CONNECTING -> CONNECTING
                    DeviceConnectionState.CONNECTED -> CONNECTED
                    DeviceConnectionState.DISCONNECTING -> DISCONNECTING
                    DeviceConnectionState.DISCONNECTED -> DISCONNECTED
                }
            }
        }
    }

    enum class UploadState {
        COMPLETE,
        NEEDED
    }

    val T1 : MutableState<String> = mutableStateOf("")
    val T2 : MutableState<String> = mutableStateOf("")
    val T3 : MutableState<String> = mutableStateOf("")
    val T4 : MutableState<String> = mutableStateOf("")
    val T5 : MutableState<String> = mutableStateOf("")
    val T6 : MutableState<String> = mutableStateOf("")
    val T7 : MutableState<String> = mutableStateOf("")
    val T8 : MutableState<String> = mutableStateOf("")

    /**
     * Updates this data object with the state update from the DeviceManager.
     *
     * @param state state update from the DeviceManager.
     */
    fun updateProbeState(state: Probe, downloads: Int) {
        macAddress.value = state.mac
        firmwareVersion.value = state.fwVersion
        hardwareRevision.value = state.hwRevision
        connectionState.value = ConnectionState.fromDeviceConnectionState(state.connectionState)
        rssi.value = state.rssi
        recordsDownloaded.value = downloads
        color.value = state.color.toString()
        id.value = state.id.toString()
        batteryStatus.value = state.batteryStatus.toString()

        instantRead.value = if(state.instantRead != null) {
            String.format("%.1f", state.instantRead?.let { convertTemperature(it) })
        } else {
            "---"
        }

        if(state.temperatures != null) {
            state.temperatures?.let {
                T1.value = String.format("%.1f", convertTemperature(it.values[0]))
                T2.value = String.format("%.1f", convertTemperature(it.values[1]))
                T3.value = String.format("%.1f", convertTemperature(it.values[2]))
                T4.value = String.format("%.1f", convertTemperature(it.values[3]))
                T5.value = String.format("%.1f", convertTemperature(it.values[4]))
                T6.value = String.format("%.1f", convertTemperature(it.values[5]))
                T7.value = String.format("%.1f", convertTemperature(it.values[6]))
                T8.value = String.format("%.1f", convertTemperature(it.values[7]))
            }
        } else {
            T1.value = "---"
            T2.value = "---"
            T3.value = "---"
            T4.value = "---"
            T5.value = "---"
            T6.value = "---"
            T7.value = "---"
            T8.value = "---"
        }


        uploadStatus.value = if(state.uploadState is ProbeUploadState.ProbeUploadInProgress) {
            val inProgress = state.uploadState as ProbeUploadState.ProbeUploadInProgress
            val percent = ((inProgress.recordsTransferred.toFloat() / inProgress.recordsRequested.toFloat()) * 100.0).toInt()

            "$percent% of ${inProgress.recordsRequested}"
        }
        else if(state.uploadState is ProbeUploadState.ProbeUploadComplete){
            UploadState.COMPLETE.toString()
        }
        else {
            UploadState.NEEDED.toString()
        }

        recordRange.value = if(state.connectionState == DeviceConnectionState.CONNECTED)
            "${state.minSequence} : ${state.maxSequence}"
        else
            ""
    }

    /**
     * Converts the input temperature in Celsius to the user's current units preference
     *
     * @param temp Temperature in C
     * @return temperature in preferred units.
     */
    private fun convertTemperature(temp: Double) : Double {
        return if(units.value == Units.CELSIUS)
            temp
        else
            (temp * 1.8) + 32.0
    }
}

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
    val onUnitsClick: (ProbeState) -> Unit,
    val onBluetoothClick: (ProbeState) -> Unit,
    val onSetProbeColorClick: (String, ProbeColor) -> Unit,
    val onSetProbeIDClick: (String, ProbeID) -> Unit
)

@Composable
fun DevicesScreen(
    appState: AppState
) {
    val viewModel : DevicesViewModel = viewModel(
        factory = DevicesViewModel.Factory(DeviceManager.instance)
    )
    val screenState = DevicesScreenState(
        probes = remember { viewModel.probes },
        onUnitsClick = { device ->
            viewModel.toggleUnits(device)
        },
        onBluetoothClick = { device ->
            viewModel.toggleConnection(device)
        },
        onSetProbeColorClick = { serialNumber, color ->
            viewModel.setProbeColor(serialNumber, color)
        },
        onSetProbeIDClick = { serialNumber, id ->
            viewModel.setProbeID(serialNumber, id)
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
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    tint = Combustion_Red
                )
            }
        },
        actionIcons = {
            IconButton(onClick = appState.navigateToSettings()) {
                Icon(
                    tint = MaterialTheme.colors.onBackground,
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings_24),
                    contentDescription = null
                )
            }
        },
        appState = appState
    ) {
        DevicesList(
            noDevicesReasonString = appState.noDevicesReasonString,
            screenState = screenState
        )
    }
}

@Composable
fun DevicesList(
    noDevicesReasonString: String,
    screenState: DevicesScreenState
) {
    val list = screenState.probes.values.toMutableStateList()

    var showProbeColorDialog by remember { mutableStateOf(false) }
    var showProbeIDDialog by remember { mutableStateOf(false) }
    var selectedProbeSerial by remember { mutableStateOf("") }

    if (showProbeColorDialog) {
        SingleSelectDialog(title = "Select Probe Color",
            optionsList = ProbeColor.stringValues(),
            defaultSelected = 0,
            submitButtonText = "OK",
            onSubmitButtonClick = {
                val selectedColor = ProbeColor.fromRaw(it.toUInt())
                screenState.onSetProbeColorClick(selectedProbeSerial, selectedColor)
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
                screenState.onSetProbeIDClick(selectedProbeSerial, selectedID)
                showProbeIDDialog = false
            },
            onDismissRequest = { showProbeIDDialog = false })
    }

    if (list.size == 0) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                color = MaterialTheme.colors.onPrimary,
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.large_padding),
                        vertical = dimensionResource(id = R.dimen.small_padding)
                    ),
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.subtitle2,
                text = noDevicesReasonString
            )
        }
    }
    else {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
        ) {
            items(list) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.large_padding),
                            vertical = dimensionResource(id = R.dimen.small_padding)
                        ),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
                ) {
                    var troubleshootingIsVisible by remember {
                        mutableStateOf(true)
                    }
                    Column(
                        modifier = Modifier.clickable { troubleshootingIsVisible = !troubleshootingIsVisible }
                    ) {
                        HeaderRow(
                            probeUiState = item,
                            onBluetoothClick = { screenState.onBluetoothClick(item) },
                            onUnitsClick = { screenState.onUnitsClick(item) }
                        )
                        CurrentTemperaturesRow(
                            probeUiState = item
                        )
                        if(troubleshootingIsVisible) {
                            TroubleshootingDataRow(
                                probeUiState = item,
                                onSetProbeColorClick = {
                                    showProbeColorDialog = true
                                    selectedProbeSerial = item.serialNumber
                                },
                                onSetProbeIDClick = {
                                    showProbeIDDialog = true
                                    selectedProbeSerial = item.serialNumber
                                }
                            )
                        } else {
                            Box(modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    probeUiState: ProbeState,
    onBluetoothClick: () -> Unit,
    onUnitsClick: () -> Unit
) {
    val unitsText = when(probeUiState.units.value) {
        ProbeState.Units.CELSIUS -> stringResource(R.string.celsius_label)
        ProbeState.Units.FAHRENHEIT -> stringResource(R.string.fahrenheit_label)
    }

    val bluetoothIcon = when(probeUiState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> painterResource(R.drawable.ic_bluetooth_disabled_24)
        ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_searching_24)
        ProbeState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_searching_24)
        ProbeState.ConnectionState.CONNECTING -> painterResource(R.drawable.ic_bluetooth_connected_24)
        ProbeState.ConnectionState.CONNECTED -> painterResource(R.drawable.ic_bluetooth_connected_24)
        ProbeState.ConnectionState.DISCONNECTING -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeState.ConnectionState.DISCONNECTED -> painterResource(R.drawable.ic_bluetooth_24)
    }

    val bluetoothIconColor = when(probeUiState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> MaterialTheme.colors.onPrimary
        ProbeState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.CONNECTING -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.CONNECTED -> MaterialTheme.colors.onPrimary
        ProbeState.ConnectionState.DISCONNECTING -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.DISCONNECTED -> MaterialTheme.colors.onPrimary
    }

    Row {
        TextButton(
            onClick = onUnitsClick,
            modifier = Modifier
                .weight(1.0f)
                .align(Alignment.CenterVertically),
        ) {
            Text(
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                text = unitsText
            )
        }
        Text(
            modifier = Modifier
                .weight(6.0f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            text = probeUiState.serialNumber
        )
        IconButton(
            onClick = onBluetoothClick,
            modifier = Modifier
                .weight(1.0f)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = bluetoothIcon,
                tint = bluetoothIconColor,
                contentDescription = "Bluetooth",
            )
        }
    }
    Row {
        Spacer(Modifier.weight(1.0f))
        Image(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(
                    start = 0.dp,
                    top = dimensionResource(id = R.dimen.large_padding),
                    end = 0.dp,
                    bottom = dimensionResource(id = R.dimen.large_padding)
                )
                .width(150.dp),
            painter = painterResource(id = R.drawable.ic_probe_horizontal),
            contentDescription = "Horizontal ProbeManager Image"
        )
        Spacer(Modifier.weight(1.0f))
    }
}

@Composable
fun CurrentTemperaturesRow(
    probeUiState: ProbeState
) {
    val color = when(probeUiState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row {
        TemperatureReading("Instant Read", probeUiState.instantRead, color, Modifier.weight(1.0f))
    }
    Row {
        TemperatureReading("T1", probeUiState.T1, color, Modifier.weight(1.0f))
        TemperatureReading("T2", probeUiState.T2, color, Modifier.weight(1.0f))
        TemperatureReading("T3", probeUiState.T3, color, Modifier.weight(1.0f))
        TemperatureReading("T4", probeUiState.T4, color, Modifier.weight(1.0f))
    }
    Row {
        TemperatureReading("T5", probeUiState.T5, color, Modifier.weight(1.0f))
        TemperatureReading("T6", probeUiState.T6, color, Modifier.weight(1.0f))
        TemperatureReading("T7", probeUiState.T7, color, Modifier.weight(1.0f))
        TemperatureReading("T8", probeUiState.T8, color, Modifier.weight(1.0f))
    }
}

@Composable
fun TemperatureReading(
    label: String,
    value: State<String>,
    color: Color,
    modifier: Modifier,
) {
    Column(modifier) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.h6,
            text = label
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = color,
            style = MaterialTheme.typography.subtitle2,
            text = value.value
        )
    }
}

@Composable
fun TroubleshootingDataItem(
    label: String,
    value: String,
    color: Color,
) {
    Row {
        Text(
            modifier = Modifier
                .weight(1.0f)
                .padding(start = 12.dp, top = 4.dp),
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Left,
            text = label
        )
        Text(
            modifier = Modifier
                .weight(1.0f)
                .padding(end = 12.dp, top = 4.dp),
            color = color,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Right,
            text = value
        )
    }
}

@Composable
fun TroubleshootingButtonsItem(
    leftLabel: String,
    leftColor: Color,
    leftHandler: () -> Unit,
    rightLabel: String,
    rightColor: Color,
    rightHandler: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .weight(1.0f)
                .padding(start = 12.dp, top = 4.dp)
                .selectable(
                    selected = false,
                    onClick = leftHandler
                ),
            color = leftColor,
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Left,
            text = leftLabel
        )
        Text(
            modifier = Modifier
                .weight(1.0f)
                .padding(end = 12.dp, top = 4.dp)
                .selectable(
                    selected = false,
                    onClick = rightHandler
                ),
            color = rightColor,
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Right,
            text = rightLabel
        )
    }
}

@Composable
fun TroubleshootingDataRow(
    probeUiState: ProbeState,
    onSetProbeColorClick: () -> Unit,
    onSetProbeIDClick: () -> Unit
) {
    val emptyHandler: () -> Unit = { }
    val color = when(probeUiState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    val setCommandColor = if(probeUiState.connectionState.value == ProbeState.ConnectionState.CONNECTED)
        MaterialTheme.colors.onPrimary
    else
        MaterialTheme.colors.onSecondary

    val setColorHandler = if(probeUiState.connectionState.value == ProbeState.ConnectionState.CONNECTED)
        onSetProbeColorClick
    else
        emptyHandler

    val setIDHandler = if(probeUiState.connectionState.value == ProbeState.ConnectionState.CONNECTED)
        onSetProbeIDClick
    else
        emptyHandler

    Row(modifier = Modifier
        .padding(vertical = dimensionResource(id = R.dimen.large_padding))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                TroubleshootingDataItem(
                    label = "Color",
                    value = probeUiState.color.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "ID",
                    value = probeUiState.id.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Upload Status",
                    value = probeUiState.uploadStatus.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Record Count",
                    value = probeUiState.recordsDownloaded.value.toString(),
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Record Range",
                    value = probeUiState.recordRange.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Firmware",
                    value = probeUiState.firmwareVersion.value ?: "",
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Hardware",
                    value = probeUiState.hardwareRevision.value ?: "",
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Battery",
                    value = probeUiState.batteryStatus.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "RSSI",
                    value = probeUiState.rssi.value.toString(),
                    color = color
                )
                TroubleshootingDataItem(
                    label = "MAC",
                    value = probeUiState.macAddress.value,
                    color = color
                )
                TroubleshootingButtonsItem(
                    leftLabel = "Set Probe Color",
                    leftColor = setCommandColor,
                    leftHandler = setColorHandler,
                    rightLabel = "Set Probe ID",
                    rightColor = setCommandColor,
                    rightHandler = setIDHandler
                )
            }
        }
    }
}
