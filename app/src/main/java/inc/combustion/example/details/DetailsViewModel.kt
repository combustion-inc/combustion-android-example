/*
 * Project: Combustion Inc. Android Example
 * File: DetailsViewModel.kt
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

package inc.combustion.example.details

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.combustion.example.BuildConfig
import inc.combustion.example.LOG_TAG
import inc.combustion.example.components.ProbeState
import inc.combustion.framework.service.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class DetailsViewModel (
    val serialNumber: String,
    private val deviceManager: DeviceManager,
    temperatureUnitsConversion: (Double) -> Double
) : ViewModel() {

    private var collectJob: Job? = null

    val probeState = ProbeState(serialNumber, temperatureUnitsConversion)
    val probeData = mutableStateListOf<LoggedProbeDataPoint>()
    val probeDataStartTimestamp = mutableStateOf(Date())

    val predictionTargetTemperatureC: Double
        get() {
            if(probeState.predictionMode.value == ProbePredictionMode.NONE.toString()) {
                return 54.5
            }

            return probeState.rawSetPointTemperatureC.value
        }

    companion object {
        const val MINIMUM_PREDICTION_SETPOINT_CELSIUS = DeviceManager.MINIMUM_PREDICTION_SETPOINT_CELSIUS
        const val MAXIMUM_PREDICTION_SETPOINT_CELSIUS = DeviceManager.MAXIMUM_PREDICTION_SETPOINT_CELSIUS
    }

    init {
        updateProbeState(null)
        viewModelScope.launch {
            deviceManager.probeFlow(serialNumber)?.collect { it ->
                val recordCount = deviceManager.recordsDownloaded(serialNumber)

                updateProbeState(it)

                if(it.uploadState is ProbeUploadState.ProbeUploadComplete) {
                    if(collectJob == null) {
                        probeData.clear()
                        probeDataStartTimestamp.value = deviceManager.logStartTimestampForDevice(serialNumber)

                        val flow = deviceManager.createLogFlowForDevice(serialNumber)
                        collectJob = viewModelScope.launch {
                            flow.collect { log ->
                                probeData.add(log)
                            }
                        }
                    }
                }
                else if(it.uploadState !is ProbeUploadState.ProbeUploadInProgress) {
                    collectJob?.cancel()
                    collectJob = null

                    if (recordCount > probeData.count()){
                        probeData.clear()
                        probeDataStartTimestamp.value = deviceManager.logStartTimestampForDevice(serialNumber)

                        val logs = deviceManager.exportLogsForDevice(serialNumber)
                        logs?.let {
                            probeData.addAll(logs)
                        }
                    }
                }
            }
        }
    }

    class Factory(
        private val deviceManager: DeviceManager,
        private val serialNumber: String,
        private val temperatureUnitsConversion: (Double) -> Double
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(serialNumber, deviceManager, temperatureUnitsConversion) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    /**
     * ViewModel processing of Bluetooth Button click from the Card composable on the DevicesScreen
     *
     * @see ProbeState
     */
    fun toggleConnection() {
        when (probeState.connectionState.value) {
            ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> {
                deviceManager.connect(serialNumber)
            }
            ProbeState.ConnectionState.CONNECTED -> {
                deviceManager.disconnect(serialNumber)
            }
            // Otherwise, we cannot change the Connect State of the device.
            else -> {
                Log.d(
                    LOG_TAG,
                    "No toggle action $serialNumber is ${probeState.connectionState.value}"
                )
            }
        }
    }

    /**
     * ViewModel processing a color choice for a probe
     *
     * @param color new color value for probe
     *
     * @see ProbeState
     * @see ProbeColor
     */
    fun setProbeColor(color: ProbeColor) {
        deviceManager.setProbeColor(serialNumber, color) {
            if(!it) {
                Log.e(LOG_TAG, "Failed to set probe color ($serialNumber)")
            }
        }
    }

    /**
     * ViewModel processing a id choice for a probe
     *
     * @param id new id value for probe
     *
     * @see ProbeState
     * @see ProbeID
     */
    fun setProbeID(id: ProbeID) {
        deviceManager.setProbeID(serialNumber, id) {
            if(!it) {
                Log.e(LOG_TAG, "Failed to set probe ID ($serialNumber)")
            }
        }
    }

    /**
     * Gets the probe data as a shareable CSV file
     *
     * @return first: suggested file name, second: csv data.
     */
    fun getShareData(): Pair<String, String> {
        val appVersionName = "${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_NAME} ${BuildConfig.BUILD_TYPE}"
        val (fileName, csvData) = deviceManager.exportLogsForDeviceAsCsv(probeState.serialNumber, appVersionName)

        return fileName to csvData
    }

    /**
     * Sets the prediction mode to removal with the input setpoint.
     *
     * @param removalTemperatureC The target removal temperature in Celsius.
     */
    fun setRemovalPrediction(removalTemperatureC: Double) {
        deviceManager.setRemovalPrediction(serialNumber, removalTemperatureC) {
            if(!it) {
                Log.e(LOG_TAG, "Failed to set removal prediction to $removalTemperatureC ($serialNumber)")
            }
        }
    }

    /**
     * Cancels the prediction if one is active.
     */
    fun cancelPrediction() {
        deviceManager.cancelPrediction(serialNumber) {
            if(!it) {
                Log.e(LOG_TAG, "Failed to cancel prediction ($serialNumber)")
            }
        }
    }

    /**
     * Updates the current value of the ProbeState with what is represented by the framework.
     *
     * @param probe Probe data object from the service.  If null, attempts to get the current
     * value from the probe.
     */
    private fun updateProbeState(probe: Probe?) {
        val recordCount = deviceManager.recordsDownloaded(serialNumber)

        (probe ?: deviceManager.probe(serialNumber))?.let {
            probeState.updateProbeState(it, recordCount)
        }
    }
}