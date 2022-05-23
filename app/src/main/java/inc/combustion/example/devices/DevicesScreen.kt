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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.example.R
import inc.combustion.framework.service.DeviceManager

@Composable
fun DevicesScreen(
    noDevicesReasonString: String
) {
    val viewModel : DevicesViewModel = viewModel(
        factory = DevicesViewModel.Factory(DeviceManager.instance)
    )
    val screenState = DevicesScreenState(
        probes = remember { viewModel.probes },
        onUnitsClick = { device ->
            viewModel.toggleUnits(device)
        }
    ) { device ->
        viewModel.toggleConnection(device)
    }

    DevicesContent(
        noDevicesReasonString = noDevicesReasonString,
        screenState = screenState
    )
}

@Composable
fun DevicesContent(
    noDevicesReasonString: String,
    screenState: DevicesScreenState
) {
    val list = screenState.probes.values.toMutableStateList()

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
                    shape = RoundedCornerShape(
                        dimensionResource(id = R.dimen.rounded_corner)
                    )
                ) {
                    Column {
                        HeaderRow(
                            probeState = item,
                            onBluetoothClick = { screenState.onBluetoothClick(item) },
                            onUnitsClick = { screenState.onUnitsClick(item) }
                        )
                        CurrentTemperaturesRow(
                            probeState = item
                        )
                        TroubleshootingDataRow(
                            probeState = item
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    probeState: ProbeState,
    onBluetoothClick: () -> Unit,
    onUnitsClick: () -> Unit
) {
    val unitsText = when(probeState.units.value) {
        ProbeState.Units.CELSIUS -> stringResource(R.string.celsius_label)
        ProbeState.Units.FAHRENHEIT -> stringResource(R.string.fahrenheit_label)
    }

    val bluetoothIcon = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> painterResource(R.drawable.ic_bluetooth_disabled_24)
        ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_searching_24)
        ProbeState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_searching_24)
        ProbeState.ConnectionState.CONNECTING -> painterResource(R.drawable.ic_bluetooth_connected_24)
        ProbeState.ConnectionState.CONNECTED -> painterResource(R.drawable.ic_bluetooth_connected_24)
        ProbeState.ConnectionState.DISCONNECTING -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeState.ConnectionState.DISCONNECTED -> painterResource(R.drawable.ic_bluetooth_24)
    }

    val bluetoothIconColor = when(probeState.connectionState.value) {
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
            text = probeState.serialNumber
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
    probeState: ProbeState
) {
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row {
        TemperatureReading("T1", probeState.T1, color, Modifier.weight(1.0f))
        TemperatureReading("T2", probeState.T2, color, Modifier.weight(1.0f))
        TemperatureReading("T3", probeState.T3, color, Modifier.weight(1.0f))
        TemperatureReading("T4", probeState.T4, color, Modifier.weight(1.0f))
    }
    Row {
        TemperatureReading("T5", probeState.T5, color, Modifier.weight(1.0f))
        TemperatureReading("T6", probeState.T6, color, Modifier.weight(1.0f))
        TemperatureReading("T7", probeState.T7, color, Modifier.weight(1.0f))
        TemperatureReading("T8", probeState.T8, color, Modifier.weight(1.0f))
    }
}

@Composable
fun TemperatureReading(
    label: String,
    value: MutableState<String>,
    color: Color,
    modifier: Modifier
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
fun TroubleshootingDataRow(
    probeState: ProbeState
) {
    Row(
        modifier = Modifier.padding(
            vertical = dimensionResource(id = R.dimen.large_padding)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if(probeState.uploadState.value == ProbeState.UploadState.IN_PROGRESS ||
               probeState.uploadState.value == ProbeState.UploadState.COMPLETE
            ) {
                Row {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LinearProgressIndicator(
                            progress = probeState.uploadProgress.value,
                            color = MaterialTheme.colors.onPrimary,
                        )

                        var label = "Upload Complete!"
                        if(probeState.uploadState.value == ProbeState.UploadState.IN_PROGRESS) {
                            label = String.format("%d of %d",
                                probeState.recordsTransferred.value.toInt(),
                                probeState.recordsRequested.value.toInt())
                        }

                        Text(
                            color = MaterialTheme.colors.onSecondary,
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Left,
                            text = label
                        )
                    }
                }
            }
            Row {
                Text(
                    modifier = Modifier
                        .weight(1.0f),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    text = "Probe"
                )
                Text(
                    modifier = Modifier
                        .weight(1.0f),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    text = "RSSI: ${probeState.rssi.value}"
                )
            }
            Row {
                val version = probeState.firmwareVersion.value ?: ""
                Text(
                    modifier = Modifier
                        .weight(1.0f),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    text = probeState.macAddress.value
                )
                Text(
                    modifier = Modifier
                        .weight(1.0f),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    text = version
                )
            }
            if(probeState.hardwareRevision.value != null) {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(1.0f),
                        color = MaterialTheme.colors.onSecondary,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        text = ""
                    )
                    Text(
                        modifier = Modifier
                            .weight(1.0f),
                        color = MaterialTheme.colors.onSecondary,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        text = probeState.hardwareRevision.value ?: ""
                    )
                }
            }

        }
    }
}
