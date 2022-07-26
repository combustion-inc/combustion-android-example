/*
 * Project: Combustion Inc. Android Example
 * File: SettingsScreen.kt
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

package inc.combustion.example.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.storage.base.SettingValueState
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import inc.combustion.example.*
import inc.combustion.example.R
import inc.combustion.example.components.AppScaffold
import inc.combustion.example.components.BackIconButton
import inc.combustion.example.devices.DevicesList
import inc.combustion.example.theme.CombustionIncEngineeringTheme
import inc.combustion.example.theme.Combustion_Red


data class SettingsScreenState(
    var isScanning: Boolean,
    val onScanningToggle: (Boolean) -> Boolean,
    var onDataCacheClear: () -> Unit,
    val versionString: String,
)

@Composable
fun SettingsScreen(
    appState: AppState
) {
    val viewModel: SettingsViewModel = viewModel()
    SettingsContent(
        appState,
        remember {
            viewModel.uiState
        }
    )
}

@Composable
fun SettingsContent(
    appState: AppState,
    screenState: SettingsScreenState,
) {
    AppScaffold(
        title = "Settings",
        navigationIcon = { BackIconButton(onClick = { appState.navigateBack() }) },
        actionIcons = { },
        appState = appState
    ) {
        SettingsList(
            state = screenState
        )
    }
}


@Composable
fun SettingsList(
    state: SettingsScreenState,
) {
    val checkedState: SettingValueState<Boolean> = rememberBooleanSettingState()
    val versionString: String = remember{ state.versionString }

    checkedState.value = state.isScanning

    Column {

        /*
        // TODO -- Determine if this feature should be removed.
        Divider()
        val scanningSubTitle = if(checkedState.value) "Searching for probes" else "Not searching for probes"
        SettingsSwitch(
            icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = "BLE Scanning") },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "BLE Scanning") },
            subtitle = { Text(text = scanningSubTitle) },
            state = checkedState,
            onCheckedChange = {
                checkedState.value = state.onScanningToggle(it)
            }
        )
         */
        Divider()
        SettingsMenuLink(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flame_24),
                    contentDescription = "Data Cache"
                )
            },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "Data Cache") },
            subtitle = { Text(text = "Clear device list and uploaded data") },
            onClick = {
                state.onDataCacheClear()
            }
        )
        Divider()
        SettingsMenuLink(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right_24),
                    contentDescription = "Version String"
                )
            },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "App Version") },
            subtitle = { Text(text = versionString) },
            onClick = {
                // do nothing
            }
        )
        Divider()
    }
}
