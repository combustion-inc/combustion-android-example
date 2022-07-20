/*
 * Project: Combustion Inc. Android Example
 * File: CardComponents.kt
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

package inc.combustion.example.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import inc.combustion.example.R
import inc.combustion.framework.service.LoggedProbeDataPoint

@Composable
fun AppProgressIndicator(
    reason: String
) {
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
            text = reason
        )
    }
}

@Composable
fun AppCard(
    title: String = "",
    onClick: () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.large_padding),
                vertical = dimensionResource(id = R.dimen.large_padding)
            ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.large_padding),
                    vertical = dimensionResource(id = R.dimen.large_padding)
                )
        ){
            if(title != "") {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(1.0f)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.subtitle2,
                        textAlign = TextAlign.Center,
                        text = title
                    )
                }
            }
            content()
        }
    }
}

@Composable
fun DeviceSummaryCard(
    probeState: ProbeState,
    onCardClick: () -> Unit = { },
    onConnectionClick: () -> Unit = { },
    onUnitsClick: () -> Unit = { },
) {
    AppCard(
        onClick = onCardClick
    ){
        DeviceCardTitle(
            probeState = probeState,
            onBluetoothClick = onConnectionClick,
            onUnitsClick = onUnitsClick
        )
        //AllTemperaturesMeasurements(probeState = probeState)
        ComputedMeasurements(probeState = probeState)
        AsynchronousDetails(probeState = probeState)
    }
}

@Composable
fun MeasurementsCard(
    title: String = "Measurements",
    probeState: ProbeState,
) {
    AppCard(title = title){
        SensorMeasurements(probeState = probeState)
    }
}

@Composable
fun HistoryCard(
    title: String = "History",
    probeState: ProbeState,
) {
    val data = remember{ mutableStateListOf<LoggedProbeDataPoint>() }
    AppCard(title = title){
        UploadStatusDetails(probeState = probeState)
        MeasurementsLineChart(plotData = data)
    }
}

@Composable
fun SettingsCard(
    title: String = "Settings",
    probeState: ProbeState,
    onSetProbeColorClick: () -> Unit = { },
    onSetProbeIDClick: () -> Unit = { }
) {
    AppCard(
        title = title
    ){
        ConfigurationDetails(
            probeState = probeState,
            onSetProbeColorClick = onSetProbeColorClick,
            onSetProbeIDClick = onSetProbeIDClick
        )
    }
}

@Composable
fun DeviceCardTitle(
    probeState: ProbeState,
    onBluetoothClick: () -> Unit,
    onUnitsClick: () -> Unit
) {
    Row {
        TemperatureUnitsButton(
            probeState = probeState,
            onClick = onUnitsClick,
            modifier = Modifier
                .weight(1.0f)
                .align(Alignment.CenterVertically),
        )
        Text(
            modifier = Modifier
                .weight(6.0f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            text = probeState.serialNumber
        )
        ConnectionStateButton(
            probeState = probeState,
            onClick = onBluetoothClick,
            modifier = Modifier
                .weight(1.0f)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun ComputedMeasurements(
    probeState: ProbeState
) {
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row {
        TemperatureReading(
            label = "Instant Read",
            value = probeState.instantRead,
            color = color,
            Modifier.weight(1.0f)
        )
        TemperatureReading(
            label = "Core",
            value = probeState.T1,
            color = color,
            Modifier.weight(1.0f)
        )
    }
    Row {
        TemperatureReading(
            label = "Surface",
            value = probeState.T4,
            color = color,
            Modifier.weight(1.0f)
        )
        TemperatureReading(
            label = "Ambient",
            value = probeState.T8,
            color = color,
            Modifier.weight(1.0f)
        )
    }
}

@Composable
fun AllTemperaturesMeasurements(
    probeState: ProbeState
) {
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row {
        TemperatureReading(
            label = "Instant Read",
            value = probeState.instantRead,
            color = color,
            Modifier.weight(1.0f)
        )
    }
    SensorMeasurements(
        probeState = probeState,
    )
}

@Composable
fun SensorMeasurements(
    probeState: ProbeState,
    modifier: Modifier = Modifier
) {
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    val readings = arrayOf(
        Pair("T1", probeState.T1 ),
        Pair("T2", probeState.T2 ),
        Pair("T3", probeState.T3 ),
        Pair("T4", probeState.T4 ),
        Pair("T5", probeState.T5 ),
        Pair("T6", probeState.T6 ),
        Pair("T7", probeState.T7 ),
        Pair("T8", probeState.T8 )
    )
    Row {
        TemperatureReading(readings[0].first, readings[0].second, color, modifier.weight(1.0f))
        TemperatureReading(readings[1].first, readings[1].second, color, modifier.weight(1.0f))
        TemperatureReading(readings[2].first, readings[2].second, color, modifier.weight(1.0f))
        TemperatureReading(readings[3].first, readings[3].second, color, modifier.weight(1.0f))
    }
    Row {
        TemperatureReading(readings[4].first, readings[4].second, color, modifier.weight(1.0f))
        TemperatureReading(readings[5].first, readings[5].second, color, modifier.weight(1.0f))
        TemperatureReading(readings[6].first, readings[6].second, color, modifier.weight(1.0f))
        TemperatureReading(readings[7].first, readings[7].second, color, modifier.weight(1.0f))
    }
}


@Composable
fun UploadStatusDetails(
    probeState: ProbeState
) {
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row(modifier = Modifier
        .padding(vertical = dimensionResource(id = R.dimen.large_padding))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                TroubleshootingDataItem(
                    label = "Upload Status",
                    value = probeState.uploadStatus.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Record Count",
                    value = probeState.recordsDownloaded.value.toString(),
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Record Range",
                    value = probeState.recordRange.value,
                    color = color
                )
            }
        }
    }
}

@Composable
fun ConfigurationDetails(
    probeState: ProbeState,
    onSetProbeColorClick: () -> Unit,
    onSetProbeIDClick: () -> Unit
) {
    val emptyHandler: () -> Unit = { }
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    val setCommandColor = if(probeState.connectionState.value == ProbeState.ConnectionState.CONNECTED)
        MaterialTheme.colors.onPrimary
    else
        MaterialTheme.colors.onSecondary

    val setColorHandler = if(probeState.connectionState.value == ProbeState.ConnectionState.CONNECTED)
        onSetProbeColorClick
    else
        emptyHandler

    val setIDHandler = if(probeState.connectionState.value == ProbeState.ConnectionState.CONNECTED)
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
                    label = "MAC",
                    value = probeState.macAddress.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Firmware",
                    value = probeState.firmwareVersion.value ?: "",
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Hardware",
                    value = probeState.hardwareRevision.value ?: "",
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Color",
                    value = probeState.color.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "ID",
                    value = probeState.id.value,
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

@Composable
fun AsynchronousDetails(
    probeState: ProbeState
) {
    val color = when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row(modifier = Modifier
        .padding(vertical = dimensionResource(id = R.dimen.large_padding))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                TroubleshootingDataItem(
                    label = "Battery",
                    value = probeState.batteryStatus.value,
                    color = color
                )
                TroubleshootingDataItem(
                    label = "Signal Strength",
                    value = probeState.rssi.value.toString(),
                    color = color
                )
            }
        }
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
