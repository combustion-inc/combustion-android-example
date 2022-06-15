/*
 * Project: Combustion Inc. Android Example
 * File: DevicesScreenState.kt
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

import androidx.compose.runtime.snapshots.SnapshotStateMap
import inc.combustion.framework.service.ProbeColor
import inc.combustion.framework.service.ProbeID

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
