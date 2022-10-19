/*
 * Project: Combustion Inc. Android Example
 * File: Scaffold.kt
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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import inc.combustion.example.AppState
import inc.combustion.example.R
import inc.combustion.example.theme.Combustion_Red

@Composable
fun AppScaffold(
    title: String,
    navigationIcon: @Composable () -> Unit,
    actionIcons: @Composable () -> Unit,
    appState: AppState,
    content: @Composable (PaddingValues) -> Unit = { padding ->
        appState.NavHost(Modifier.padding(padding))
    }
) {
    Scaffold (
        scaffoldState = appState.scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = navigationIcon,
                actions = {
                    actionIcons()
                }
            )
        },
        content = content
    )
}

@Composable
fun CombustionIconButton(
    onClick: () -> Unit = { }
) {
    IconButton(
        onClick = onClick,
        //modifier = Modifier.background(color = Combustion_Yellow, shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = Combustion_Red
        )
    }
}

@Composable
fun SettingsIconButton(
    onClick: () -> Unit = { }
) {
    IconButton(onClick = onClick) {
        Icon(
            tint = MaterialTheme.colors.onBackground,
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings_24),
            contentDescription = null

        )
    }
}

@Composable
fun BackIconButton(
    onClick: () -> Unit = { }
) {
    IconButton(onClick = onClick) {
        Icon(
            tint = MaterialTheme.colors.onBackground,
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null
        )
    }
}

@Composable
fun ConnectionStateButton(
    probeState: ProbeState,
    onClick: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    // TODO - Resolve UX for icon state after user feedback
    /*
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
     */

    val bluetoothIcon = when(probeState.connectionState.value) {
        // disabled bluetooth icon
        ProbeState.ConnectionState.OUT_OF_RANGE -> painterResource(R.drawable.ic_bluetooth_disabled_24)
        ProbeState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_disabled_24)

        // regular bluetooth icon
        ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeState.ConnectionState.CONNECTING -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeState.ConnectionState.CONNECTED -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeState.ConnectionState.DISCONNECTING -> painterResource(R.drawable.ic_bluetooth_24)
        ProbeState.ConnectionState.DISCONNECTED -> painterResource(R.drawable.ic_bluetooth_24)
    }

    val bluetoothIconColor = when(probeState.connectionState.value) {
        // secondary color
        ProbeState.ConnectionState.OUT_OF_RANGE -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.ADVERTISING_NOT_CONNECTABLE -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.CONNECTING -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.DISCONNECTING -> MaterialTheme.colors.onSecondary
        ProbeState.ConnectionState.DISCONNECTED -> MaterialTheme.colors.onSecondary

        // primary color
        ProbeState.ConnectionState.CONNECTED -> MaterialTheme.colors.onPrimary
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = bluetoothIcon,
            tint = bluetoothIconColor,
            contentDescription = ""
        )
    }
}

@Composable
fun ShareIconButton(
    enable: Boolean,
    onClick: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    val handler = if(enable) onClick else { {} }
    val color = when {
        enable -> MaterialTheme.colors.onPrimary
        else -> MaterialTheme.colors.onSecondary
    }

    IconButton(
        onClick = handler,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_share_24),
            tint = color,
            contentDescription = ""
        )
    }
}

@Composable
fun TemperatureUnitsButton(
    appState: AppState,
    onClick: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    val unitsText = when(appState.units.value) {
        AppState.Units.CELSIUS -> stringResource(R.string.celsius_label)
        AppState.Units.FAHRENHEIT -> stringResource(R.string.fahrenheit_label)
    }

    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            text = unitsText
        )
    }
}
