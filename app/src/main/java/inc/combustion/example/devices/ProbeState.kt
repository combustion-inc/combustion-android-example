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