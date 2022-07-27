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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import inc.combustion.example.R
import inc.combustion.framework.service.LoggedProbeDataPoint
import java.util.*

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
fun CardProgressIndicator(
    reason: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = dimensionResource(id = R.dimen.large_padding)),
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
            style = MaterialTheme.typography.body1,
            text = reason
        )
    }
}

@Composable
fun ClickableAppCard(
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
fun ExpandableAppCard(
    title: String = "",
    cardIsExpanded: MutableState<Boolean>,
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
                .clickable(onClick = { cardIsExpanded.value = !cardIsExpanded.value })
                .padding(
                    horizontal = dimensionResource(id = R.dimen.large_padding),
                    vertical = dimensionResource(id = R.dimen.large_padding)
                )
        ){
            if(title != "") {
                val icon = if(cardIsExpanded.value) {
                    ImageVector.vectorResource(id = R.drawable.ic_keyboard_arrow_up_24)
                } else {
                    ImageVector.vectorResource(id = R.drawable.ic_keyboard_arrow_down_24)
                }

                Row {
                    Text(
                        modifier = Modifier
                            .weight(2.0f)
                            .align(Alignment.CenterVertically)
                            .padding(start = 12.dp),
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.subtitle2,
                        textAlign = TextAlign.Left,
                        text = title
                    )
                    Spacer(Modifier.weight(1.0f))
                    Icon(
                        modifier = Modifier
                            .weight(0.5f)
                            .align(Alignment.CenterVertically)
                            .padding(end = 12.dp),
                        tint = MaterialTheme.colors.onBackground,
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }

            if(cardIsExpanded.value) {
                content()
            }
        }
    }
}

@Composable
fun DataColor(
    probeState: ProbeState
) : Color {
    return when(probeState.connectionState.value) {
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }
}

@Composable
fun CardDivider() {
    Divider(
        color = MaterialTheme.colors.onSecondary,
        modifier = Modifier
            .padding(start = 12.dp, top = 8 .dp),
    )
}

@Composable
fun DeviceSummaryCard(
    probeState: ProbeState,
    onCardClick: () -> Unit = { },
    onConnectionClick: () -> Unit = { },
    onUnitsClick: () -> Unit = { },
) {
    ClickableAppCard(
        onClick = onCardClick
    ){
        DeviceCardTitle(
            probeState = probeState,
            onBluetoothClick = onConnectionClick,
            onUnitsClick = onUnitsClick
        )
        //AllTemperaturesMeasurements(probeState = probeState)
        SummaryMeasurements(probeState = probeState)
        SummaryDetails(probeState = probeState)
    }
}

@Composable
fun InstantReadCard(
    title: String = "Instant Read",
    probeState: ProbeState,
    cardIsExpanded: MutableState<Boolean>,
) {
    val color = DataColor(probeState = probeState)

    ExpandableAppCard(title = title, cardIsExpanded = cardIsExpanded){
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                color = color,
                style = MaterialTheme.typography.h2,
                textAlign = TextAlign.Center,
                text = probeState.instantRead.value
            )
        }
    }
}

@Composable
fun MeasurementsCard(
    title: String = "Measurements",
    probeState: ProbeState,
    cardIsExpanded: MutableState<Boolean>,
) {
    ExpandableAppCard(title = title, cardIsExpanded = cardIsExpanded){
        SensorMeasurements(probeState = probeState)
    }
}

@Composable
fun PlotCard(
    title: String = "Plot",
    noDataDefaultReason: String = "Please Connect ...",
    noDataUploadingReason: String = "Getting Measurements ...",
    probeState: ProbeState,
    plotData: SnapshotStateList<LoggedProbeDataPoint>,
    plotDataStartTimestamp: MutableState<Date>,
    cardIsExpanded: MutableState<Boolean>,
) {
    val data = remember { plotData }
    ExpandableAppCard(title = title, cardIsExpanded = cardIsExpanded){
        MeasurementsLineChart(
            plotData = data, 
            plotDataStartTimestamp = plotDataStartTimestamp,
            noDataComposable = {
                PlotCardNoData(
                    defaultReason = noDataDefaultReason,
                    uploadingReason = noDataUploadingReason,
                    probeState = probeState
                )
            }
        )
    }
}

@Composable
fun PlotCardNoData(
    defaultReason: String,
    uploadingReason: String,
    probeState: ProbeState
) {
    val reason = when {
        probeState.isUploading.value -> uploadingReason
        else -> defaultReason
    }
    CardProgressIndicator(reason = reason)
}

@Composable
fun DetailsCard(
    title: String = "Details",
    probeState: ProbeState,
    cardIsExpanded: MutableState<Boolean>,
    onSetProbeColorClick: () -> Unit = { },
    onSetProbeIDClick: () -> Unit = { }
) {
    ExpandableAppCard(title = title, cardIsExpanded = cardIsExpanded){
        ProbeDetails(
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
fun SummaryMeasurements(
    probeState: ProbeState
) {
    val color = DataColor(probeState = probeState)

    Row {
        CardTemperature(
            label = "Instant Read",
            value = probeState.instantRead,
            color = color,
            Modifier.weight(1.0f)
        )
        CardTemperature(
            label = "Tip",
            value = probeState.T1,
            color = color,
            Modifier.weight(1.0f)
        )
    }
    Row {
        CardTemperature(
            label = "Middle",
            value = probeState.T4,
            color = color,
            Modifier.weight(1.0f)
        )
        CardTemperature(
            label = "Handle",
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
    val color = DataColor(probeState = probeState)

    Row {
        CardTemperature(
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
    val color = DataColor(probeState = probeState)

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
        CardTemperature(readings[0].first, readings[0].second, color, modifier.weight(1.0f))
        CardTemperature(readings[1].first, readings[1].second, color, modifier.weight(1.0f))
        CardTemperature(readings[2].first, readings[2].second, color, modifier.weight(1.0f))
        CardTemperature(readings[3].first, readings[3].second, color, modifier.weight(1.0f))
    }
    Row {
        CardTemperature(readings[4].first, readings[4].second, color, modifier.weight(1.0f))
        CardTemperature(readings[5].first, readings[5].second, color, modifier.weight(1.0f))
        CardTemperature(readings[6].first, readings[6].second, color, modifier.weight(1.0f))
        CardTemperature(readings[7].first, readings[7].second, color, modifier.weight(1.0f))
    }
}


@Composable
fun ProbeDetails(
    probeState: ProbeState,
    onSetProbeColorClick: () -> Unit,
    onSetProbeIDClick: () -> Unit
) {
    val emptyHandler: () -> Unit = { }
    val color = DataColor(probeState = probeState)

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
                CardDataItem(
                    label = "Connection",
                    value = probeState.connectionDescription.value,
                    color = color
                )
                CardDataItem(
                    label = "Battery Level",
                    value = probeState.batteryStatus.value,
                    color = color
                )
                CardDataItem(
                    label = "Signal Strength",
                    value = probeState.rssi.value.toString(),
                    color = color
                )
                CardDivider()
                CardDataItem(
                    label = "Color",
                    value = probeState.color.value,
                    color = color
                )
                CardDataItem(
                    label = "ID",
                    value = probeState.id.value,
                    color = color
                )
                CardDivider()
                CardDataItem(
                    label = "Data Upload",
                    value = probeState.uploadStatus.value,
                    color = color
                )
                CardDataItem(
                    label = "Record Count",
                    value = probeState.recordsDownloaded.value.toString(),
                    color = color
                )
                CardDataItem(
                    label = "Record Range",
                    value = probeState.recordRange.value,
                    color = color
                )
                CardDataItem(
                    label = "Sample Period",
                    value = probeState.samplePeriod.value,
                    color = color
                )
                CardDivider()
                CardDataItem(
                    label = "Firmware",
                    value = probeState.firmwareVersion.value ?: "",
                    color = color
                )
                CardDataItem(
                    label = "Hardware",
                    value = probeState.hardwareRevision.value ?: "",
                    color = color
                )
                CardDataItem(
                    label = "MAC",
                    value = probeState.macAddress.value,
                    color = color
                )
                CardDivider()
                CardTwoButtons(
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
fun SummaryDetails(
    probeState: ProbeState
) {
    val color = DataColor(probeState = probeState)

    Row(modifier = Modifier
        .padding(vertical = dimensionResource(id = R.dimen.large_padding))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                CardDataItem(
                    label = "Battery Level",
                    value = probeState.batteryStatus.value,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CardTemperature(
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
fun CardDataItem(
    label: String,
    value: String,
    color: Color,
) {
    Row {
        Text(
            modifier = Modifier
                .weight(1.0f)
                .padding(start = 12.dp, top = 4.dp),
            color = color,
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
fun CardTwoButtons(
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
