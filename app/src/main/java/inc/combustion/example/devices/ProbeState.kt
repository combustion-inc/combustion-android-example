/*
 * Project: Combustion Inc. Android Example
 * File: ProbeState.kt
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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import inc.combustion.framework.service.DeviceConnectionState
import inc.combustion.framework.service.ProbeUploadState
import inc.combustion.framework.service.Probe

/**
 * Observable state data object for a probe.  Binds the state between the DeviceScreen's ViewModel
 * and Composable functions.  All of the properties in this class are observable using Kotlin State.
 *
 * @property serialNumber serial number.
 * @property macAddress Bluetooth MAC address.
 * @property firmwareVersion firmware version.
 * @property rssi current received signal strength.
 * @property temperaturesCelsius current temperature values in Celsius.
 * @property connectionState current connect state.
 * @property units user's temperature units preference.
 * @property recordsTransferred number of records transferred during current upload.
 * @property recordsRequested number of records requested during current upload.
 * @property uploadProgress upload progress percentage.
 * @property uploadState current upload progress state.
 */
data class ProbeState(
    val serialNumber: String,

    var macAddress: MutableState<String> = mutableStateOf("TBD"),
    var firmwareVersion: MutableState<String?> = mutableStateOf(null),
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
    var recordsTransferred: MutableState<UInt> = mutableStateOf(0u),
    var recordsRequested: MutableState<UInt> = mutableStateOf(0u),
    var uploadProgress: MutableState<Float> = mutableStateOf(0.0f),
    var uploadState: MutableState<UploadState> = mutableStateOf(UploadState.NEEDED),
) {
    /**
     * Enumerates units preference.
     *
     * @property string Readable form of enumeration value.
     */
    enum class Units(val string: String) {
        FAHRENHEIT("Fahrenheit"),
        CELSIUS("Celsius")
    }

    /**
     * Adapts framework ConnectionState
     *
     * @property string Readable form of enumeration value.
     */
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

    /**
     * Adapts framework UploadState
     */
    enum class UploadState {
        IN_PROGRESS,
        COMPLETE,
        NEEDED
    }

    /**
     * T1 sensor reading from the thermometer.  Closest to tip end of thermometer.
     */
    val T1 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(0)))

    /**
     * T2 sensor reading from the thermometer.
     */
    val T2 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(1)))

    /**
     * T3 sensor reading from the thermometer.
     */
    val T3 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(2)))

    /**
     * T4 sensor reading from the thermometer.
     */
    val T4 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(3)))

    /**
     * T5 sensor reading from the thermometer.
     */
    val T5 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(4)))

    /**
     * T6 sensor reading from the thermometer.
     */
    val T6 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(5)))

    /**
     * T7 sensor reading from the thermometer.
     */
    val T7 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(6)))

    /**
     * T8 sensor reading from the thermometer.  Closest to handle end of thermometer.
     */
    val T8 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(7)))

    /**
     * Updates this data object with the state update from the DeviceManager.
     *
     * @param state state update from the DeviceManager.
     */
    fun updateProbeState(state: Probe) {
        macAddress.value = state.mac
        firmwareVersion.value = state.fwVersion
        connectionState.value = ConnectionState.fromDeviceConnectionState(state.connectionState)
        rssi.value = state.rssi
        temperaturesCelsius.clear()
        temperaturesCelsius.addAll(state.temperatures.values)

        if(state.uploadState is ProbeUploadState.ProbeUploadInProgress) {
            val uploadProgressState = state.uploadState as ProbeUploadState.ProbeUploadInProgress
            uploadProgress.value =
                uploadProgressState.recordsTransferred.toFloat() /
                        uploadProgressState.recordsRequested.toFloat()
            recordsTransferred.value = uploadProgressState.recordsTransferred
            recordsRequested.value = uploadProgressState.recordsRequested
            uploadState.value = UploadState.IN_PROGRESS
        }
        else if(state.uploadState is ProbeUploadState.ProbeUploadComplete){
            uploadProgress.value = 1.0f
            recordsTransferred.value = 0u
            recordsRequested.value = 0u
            uploadState.value = UploadState.COMPLETE
        }
        else {
            uploadProgress.value = 0.0f
            recordsTransferred.value = 0u
            recordsRequested.value = 0u
            uploadState.value = UploadState.NEEDED
        }
    }

    /**
     * Units conversion for the specified sensor.
     *
     * @param index index of the sensor on the probe.
     * @return temperature reading in user preferred units.
     */
    private fun getTemperature(index: Int) : Double {
        return if(units.value == Units.CELSIUS)
            temperaturesCelsius[index]
        else
            (temperaturesCelsius[index] * 1.8) + 32.0
    }
}