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

package inc.combustion.example.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import inc.combustion.framework.service.*

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
 * @property connectionDescription friendly description of connection state
 * @property samplePeriod: the normal data sample period
 */
data class ProbeState(
    val serialNumber: String,
    val macAddress: MutableState<String> = mutableStateOf("TBD"),
    val firmwareVersion: MutableState<String?> = mutableStateOf(null),
    val hardwareRevision: MutableState<String?> = mutableStateOf(null),
    val rssi: MutableState<Int> = mutableStateOf(0),
    val temperaturesCelsius: SnapshotStateList<Double> = mutableStateListOf(
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0
    ),
    val connectionState: MutableState<ConnectionState> = mutableStateOf(ConnectionState.OUT_OF_RANGE),
    val units: MutableState<Units> = mutableStateOf(Units.FAHRENHEIT),
    val uploadStatus: MutableState<String> = mutableStateOf(""),
    val recordsDownloaded: MutableState<Int> = mutableStateOf(0),
    val recordRange: MutableState<String> = mutableStateOf(""),
    val color: MutableState<String> = mutableStateOf(""),
    val id: MutableState<String> = mutableStateOf(""),
    val batteryStatus: MutableState<String> = mutableStateOf(""),
    val instantRead: MutableState<String> = mutableStateOf(""),
    val connectionDescription: MutableState<String> = mutableStateOf(""),
    val samplePeriod: MutableState<String> = mutableStateOf("0.0"),
    val virtualCoreSensor: MutableState<String> = mutableStateOf(""),
    val virtualSurfaceSensor: MutableState<String> = mutableStateOf(""),
    val hopCount: MutableState<String> = mutableStateOf(""),
    val coreTemperature: MutableState<String> = mutableStateOf(""),
    val surfaceTemperature: MutableState<String> = mutableStateOf(""),
    val ambientTemperature: MutableState<String> = mutableStateOf(""),
    val predictionState: MutableState<String> = mutableStateOf(""),
    val predictionMode: MutableState<String> = mutableStateOf(""),
    val predictionType: MutableState<String> = mutableStateOf(""),
    val setPointTemperatureC: MutableState<String> = mutableStateOf(""),
    val heatStartTemperatureC: MutableState<String> = mutableStateOf(""),
    val percentThroughCook: MutableState<String> = mutableStateOf(""),
    val prediction: MutableState<String> = mutableStateOf(""),
    val estimateCoreC: MutableState<String> = mutableStateOf("")
) {
    enum class Units(val string: String) {
        FAHRENHEIT("Fahrenheit"),
        CELSIUS("Celsius")
    }

    enum class ConnectionState {
        OUT_OF_RANGE,
        ADVERTISING_CONNECTABLE,
        ADVERTISING_NOT_CONNECTABLE,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED;

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

    val T1 : MutableState<String> = mutableStateOf("")
    val T2 : MutableState<String> = mutableStateOf("")
    val T3 : MutableState<String> = mutableStateOf("")
    val T4 : MutableState<String> = mutableStateOf("")
    val T5 : MutableState<String> = mutableStateOf("")
    val T6 : MutableState<String> = mutableStateOf("")
    val T7 : MutableState<String> = mutableStateOf("")
    val T8 : MutableState<String> = mutableStateOf("")

    val isUploading = mutableStateOf(false)

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
        isUploading.value = (state.uploadState is ProbeUploadState.ProbeUploadInProgress)

        samplePeriod.value = if(state.sessionInfo != null) {
            String.format("%d ms", state.sessionInfo?.let { it.samplePeriod.toLong() } )
        } else {
            ""
        }

        // convert to friendly string
        batteryStatus.value = when(state.batteryStatus) {
            ProbeBatteryStatus.LOW_BATTERY -> "Low"
            ProbeBatteryStatus.OK -> "Good"
        }

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

        coreTemperature.value = state.coreTemperature?.let {
            String.format("%.1f", convertTemperature(it))
        } ?: run {
           "---"
        }

        surfaceTemperature.value = state.surfaceTemperature?.let {
            String.format("%.1f", convertTemperature(it))
        } ?: run {
            "---"
        }

        ambientTemperature.value = state.ambientTemperature?.let {
            String.format("%.1f", convertTemperature(it))
        } ?: run {
            "---"
        }

        // convert to friendly string
        uploadStatus.value = when(state.uploadState)  {
            is ProbeUploadState.ProbeUploadInProgress -> {
                val inProgress = state.uploadState as ProbeUploadState.ProbeUploadInProgress
                val percent = ((inProgress.recordsTransferred.toFloat() / inProgress.recordsRequested.toFloat()) * 100.0).toInt()
                "$percent% of ${inProgress.recordsRequested}"
            }
            is ProbeUploadState.ProbeUploadComplete -> "Upload Complete"
            else -> "Please Connect"
        }

        // convert to friendly string
        recordRange.value = when(state.connectionState) {
            DeviceConnectionState.CONNECTED -> "${state.minSequence} : ${state.maxSequence}"
            else -> ""
        }

        connectionDescription.value = when(state.connectionState) {
            DeviceConnectionState.OUT_OF_RANGE -> "Not Available"
            DeviceConnectionState.ADVERTISING_CONNECTABLE -> "Available"
            DeviceConnectionState.ADVERTISING_NOT_CONNECTABLE -> "Not Available"
            DeviceConnectionState.CONNECTING -> "Available"
            DeviceConnectionState.CONNECTED -> "Connected"
            DeviceConnectionState.DISCONNECTING -> "Connected"
            DeviceConnectionState.DISCONNECTED -> "Disconnected"
        }

        virtualCoreSensor.value = state.virtualSensors.virtualCoreSensor.toString()
        virtualSurfaceSensor.value = state.virtualSensors.virtualSurfaceSensor.toString()
        hopCount.value = state.hopCount.toString()

        predictionState.value = state.predictionState?.let {
            when(it) {
                ProbePredictionState.PROBE_NOT_INSERTED -> "Not Inserted"
                ProbePredictionState.PROBE_INSERTED -> "Inserted"
                ProbePredictionState.WARMING -> "Warming"
                ProbePredictionState.PREDICTING -> "Predicting"
                ProbePredictionState.REMOVAL_PREDICTION_DONE -> "Ready to Remove"
                ProbePredictionState.RESERVED_STATE_5 -> "Reserved 5"
                ProbePredictionState.RESERVED_STATE_6 -> "Reserved 6"
                ProbePredictionState.RESERVED_STATE_7 -> "Reserved 7"
                ProbePredictionState.RESERVED_STATE_8 -> "Reserved 8"
                ProbePredictionState.RESERVED_STATE_9 -> "Reserved 9"
                ProbePredictionState.RESERVED_STATE_10 -> "Reserved 10"
                ProbePredictionState.RESERVED_STATE_11 ->  "Reserved 11"
                ProbePredictionState.RESERVED_STATE_12 -> "Reserved 12"
                ProbePredictionState.RESERVED_STATE_13 -> "Reserved 13"
                ProbePredictionState.RESERVED_STATE_14 -> "Reserved 14"
                ProbePredictionState.UNKNOWN -> "Unknown"
            }
        } ?: run { "" }

        predictionMode.value = state.predictionMode?.let { it.toString() } ?: run { ProbePredictionMode.NONE.toString() }
        predictionType.value = state.predictionType?.let { it.toString() } ?: run { ProbePredictionType.NONE.toString() }

        setPointTemperatureC.value = state.setPointTemperatureC?.let {
            String.format("%.1f", convertTemperature(it))
        } ?: run {
            ""
        }

        heatStartTemperatureC.value = state.heatStartTemperatureC?.let {
            String.format("%.1f", convertTemperature(it))
        } ?: run {
            "---"
        }

        prediction.value = state.predictionS?.let {
            String.format("%02d:%02d", it.toInt() / 60, it.toInt() % 60)
        } ?: run {
            "--:--"
        }

        estimateCoreC.value = state.estimatedCoreC?.let {
            String.format("%.1f", convertTemperature(it))
        } ?: run {
            "---"
        }

        if(state.heatStartTemperatureC != null && state.estimatedCoreC != null && state.setPointTemperatureC != null) {
            val start = state.heatStartTemperatureC!!
            val end = state.setPointTemperatureC!!
            val core = state.estimatedCoreC!!
            percentThroughCook.value = "${(((core - start) / (end - start)) * 100.0).toInt()} %"
        } else {
            percentThroughCook.value = ""
        }
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
