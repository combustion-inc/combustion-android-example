/*
 * Project: Combustion Inc. Android Example
 * File: SettingsViewModel.kt
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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import inc.combustion.example.BuildConfig
import inc.combustion.framework.Combustion
import inc.combustion.framework.service.DeviceManager

class SettingsViewModel : ViewModel() {

    val uiState by mutableStateOf(
        SettingsScreenState(
            isScanning = DeviceManager.instance.isScanningForDevices,
            onScanningToggle = { setScanning(it) },
            onDataCacheClear = { DeviceManager.instance.clearDevices() },
            versionString = "${BuildConfig.VERSION_NAME} ${BuildConfig.BUILD_TYPE}",
            frameworkVersionString = "${Combustion.FRAMEWORK_VERSION_NAME} ${Combustion.FRAMEWORK_BUILD_TYPE}"
        )
    )

    private fun setScanning(toOn: Boolean): Boolean {
        when {
            toOn -> uiState.isScanning = DeviceManager.instance.startScanningForProbes()
            else -> uiState.isScanning = DeviceManager.instance.stopScanningForProbes()
        }
        return uiState.isScanning
    }
}
