package inc.combustion.engineering.ui.devices

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import inc.combustion.engineering.ui.CombustionAppState
import inc.combustion.engineering.ui.CombustionAppContent
import inc.combustion.engineering.ui.rememberCombustionAppState
import inc.combustion.engineering.ui.theme.CombustionIncEngineeringTheme
import inc.combustion.engineering.R

@Composable
fun DevicesContent(
    appState: CombustionAppState,
    screenState: DevicesScreenState
) {
    val list = screenState.probes.values.toMutableStateList()

    if(screenState.isSnackBarShowing.value) {
        val message = stringResource(id = screenState.snackBarMessage.value.resource)
        LaunchedEffect(screenState.isSnackBarShowing.value) {
            appState.scaffoldState.snackbarHostState.showSnackbar(
                message = String.format("%s %s",
                    screenState.snackBarMessage.value.id,
                    message
                )
            )
            screenState.onDismissSnackbarMessage()
        }
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
                text = appState.noDevicesReasonString
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
                            probeUiState = item,
                            onBluetoothClick = { screenState.onBluetoothClick(item) },
                            onUnitsClick = { screenState.onUnitsClick(item) }
                        )
                        CurrentTemperaturesRow(
                            probeUiState = item
                        )
                        TroubleshootingDataRow(
                            probeUiState = item
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    probeUiState: ProbeUiState,
    onBluetoothClick: () -> Unit,
    onUnitsClick: () -> Unit
) {
    val unitsText = when(probeUiState.units.value) {
        ProbeUiState.Units.CELSIUS -> stringResource(R.string.celsius_label)
        ProbeUiState.Units.FAHRENHEIT -> stringResource(R.string.fahrenheit_label)
    }

    val bluetoothIcon = when(probeUiState.connectionState.value) {
        ProbeUiState.ConnectionState.OUT_OF_RANGE -> painterResource(R.drawable.ic_bluetooth_disabled_24)
        ProbeUiState.ConnectionState.ADVERTISING_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_searching_24)
        ProbeUiState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_searching_24)
        ProbeUiState.ConnectionState.CONNECTING -> painterResource(R.drawable.ic_bluetooth_connected_24)
        ProbeUiState.ConnectionState.CONNECTED -> painterResource(R.drawable.ic_bluetooth_connected_24)
        ProbeUiState.ConnectionState.DISCONNECTING -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeUiState.ConnectionState.DISCONNECTED -> painterResource(R.drawable.ic_bluetooth_24)
    }

    val bluetoothIconColor = when(probeUiState.connectionState.value) {
        ProbeUiState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        ProbeUiState.ConnectionState.ADVERTISING_CONNECTABLE -> MaterialTheme.colors.onPrimary
        ProbeUiState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> MaterialTheme.colors.onSecondary
        ProbeUiState.ConnectionState.CONNECTING -> MaterialTheme.colors.onSecondary
        ProbeUiState.ConnectionState.CONNECTED -> MaterialTheme.colors.onPrimary
        ProbeUiState.ConnectionState.DISCONNECTING -> MaterialTheme.colors.onSecondary
        ProbeUiState.ConnectionState.DISCONNECTED -> MaterialTheme.colors.onPrimary
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
    probeUiState: ProbeUiState
) {
    val color = when(probeUiState.connectionState.value) {
        ProbeUiState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        else -> MaterialTheme.colors.onPrimary
    }

    Row() {
        TemperatureReading("T1", probeUiState.T1, color, Modifier.weight(1.0f))
        TemperatureReading("T2", probeUiState.T2, color, Modifier.weight(1.0f))
        TemperatureReading("T3", probeUiState.T3, color, Modifier.weight(1.0f))
        TemperatureReading("T4", probeUiState.T4, color, Modifier.weight(1.0f))
    }
    Row() {
        TemperatureReading("T5", probeUiState.T5, color, Modifier.weight(1.0f))
        TemperatureReading("T6", probeUiState.T6, color, Modifier.weight(1.0f))
        TemperatureReading("T7", probeUiState.T7, color, Modifier.weight(1.0f))
        TemperatureReading("T8", probeUiState.T8, color, Modifier.weight(1.0f))
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
    probeUiState: ProbeUiState
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
            if(probeUiState.uploadState.value == ProbeUiState.UploadState.IN_PROGRESS ||
               probeUiState.uploadState.value == ProbeUiState.UploadState.COMPLETE
            ) {
                Row() {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LinearProgressIndicator(
                            progress = probeUiState.uploadProgress.value,
                            color = MaterialTheme.colors.onPrimary,
                        )

                        var label: String = "Upload Complete!"
                        if(probeUiState.uploadState.value == ProbeUiState.UploadState.IN_PROGRESS) {
                            label = String.format("%d of %d",
                                probeUiState.recordsTransferred.value.toInt(),
                                probeUiState.recordsRequested.value.toInt())
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
            Row() {
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
                    text = "RSSI: ${probeUiState.rssi.value}"
                )
            }
            Row {
                val version = probeUiState.firmwareVersion.value ?: ""
                Text(
                    modifier = Modifier
                        .weight(1.0f),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    text = probeUiState.macAddress.value
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    CombustionIncEngineeringTheme {
        val appState = rememberCombustionAppState()
        val devices = DevicesViewModel.previewData(null)
        val stateList = remember{ mutableStateMapOf<String, ProbeUiState>() }
        val isSnackBarShowing = remember { mutableStateOf(true) }
        val snackBarMessage = remember {
            mutableStateOf(
                DevicesViewModel.SnackBarMessage(
                "12345657", R.string.no_open_connections_message)
            )
        }

        devices.forEach{ stateList[it.serialNumber] = it }

        val screenState = DevicesScreenState(
            probes = stateList,
            isSnackBarShowing = isSnackBarShowing,
            snackBarMessage = snackBarMessage,
            onDismissSnackbarMessage = { },
            onUnitsClick = { }
        ) { }

        CombustionAppContent(
            appState = appState,
            content = @Composable {
                DevicesContent(
                    appState = appState,
                    screenState = screenState
                )
            }
        )
    }
}
