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

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.combustion.example.LOG_TAG
import inc.combustion.example.components.ProbeState
import inc.combustion.framework.service.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DetailsViewModel (
    val serialNumber: String,
    private val deviceManager: DeviceManager
) : ViewModel() {

    private var collectJob: Job? = null

    val probe = ProbeState(serialNumber)
    val probeData = mutableStateListOf<LoggedProbeDataPoint>()
    val probeDataStartTimestamp = mutableStateOf(Date())

    init {
        viewModelScope.launch {
            deviceManager.probeFlow(serialNumber)?.collect { it ->
                val recordCount = deviceManager.recordsDownloaded(serialNumber)

                probe.updateProbeState(it, recordCount)

                if(it.uploadState is ProbeUploadState.ProbeUploadComplete) {
                    if(collectJob == null) {
                        probeData.clear()
                        probeDataStartTimestamp.value = deviceManager.logStartTimestampForDevice(serialNumber)

                        val flow = deviceManager.createLogFlowForDevice(serialNumber)
                        collectJob = viewModelScope.launch {
                            flow.collect { log ->
                                val seconds = (log.timestamp.time - probeDataStartTimestamp.value.time) / 1000.0f
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
        private val serialNumber: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                return DetailsViewModel(serialNumber, deviceManager) as T
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
        when (probe.connectionState.value) {
            ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> {
                deviceManager.connect(probe.serialNumber)
            }
            ProbeState.ConnectionState.CONNECTED -> {
                deviceManager.disconnect(probe.serialNumber)
            }
            // Otherwise, we cannot change the Connect State of the device.
            else -> {
                Log.d(
                    LOG_TAG,
                    "No toggle action ${probe.serialNumber} is ${probe.connectionState.value}"
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
        Log.e(LOG_TAG, "set probe color (${probe.serialNumber})")
        deviceManager.setProbeColor(probe.serialNumber, color) {
            if(!it) {
                Log.e(LOG_TAG, "Failed to set probe color (${probe.serialNumber})")
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
        Log.e(LOG_TAG, "set probe ID (${probe.serialNumber})")
        deviceManager.setProbeID(probe.serialNumber, id) {
            if(!it) {
                Log.e(LOG_TAG, "Failed to set probe ID (${probe.serialNumber})")
            }
        }
    }

    fun getShareData(): Pair<String, String> {
        return "ProbeTest.csv" to "col1,col2,col3,col4"
    }
}