/*
 * Project: Combustion Inc. Android Example
 * File: DevicesViewModel.kt
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

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.combustion.example.LOG_TAG
import inc.combustion.framework.service.*
import inc.combustion.example.R
import kotlinx.coroutines.launch
import kotlin.IllegalArgumentException
import kotlinx.coroutines.flow.*

/**
 * ViewModel for DevicesScreen.
 *
 * @property deviceManager
 *
 * @see DevicesScreen
 */
class DevicesViewModel(
    private val deviceManager : DeviceManager,
) : ViewModel() {

    data class SnackBarMessage(
        var id : String,
        var resource : Int
    )

    var probes = mutableStateMapOf<String, ProbeState>()
        private set

    var isSnackBarShowing: MutableState<Boolean> = mutableStateOf(false)
        private set

    var snackBarMessage: MutableState<SnackBarMessage> = mutableStateOf(SnackBarMessage("", 0))
        private set

    init {
        // If the DeviceManager is already initialized and bound to the Combustion Android Service,
        // then the lambda registered here is immediately called.  Otherwise, this callback is
        // executed asynchronously when once bounds to the service.
        deviceManager.registerOnBoundInitialization {
            viewModelScope.launch {

                // Collect on the discoveredProbesFlow and handle DeviceDiscoveredEvents that
                // are produces by the service.
                deviceManager.discoveredProbesFlow.collect { event ->
                    when(event) {
                        is DeviceDiscoveredEvent.ScanningOn -> { }
                        is DeviceDiscoveredEvent.BluetoothOn -> { }
                        is DeviceDiscoveredEvent.ScanningOff -> { }

                        // When a device is discovered process it.
                        is DeviceDiscoveredEvent.DeviceDiscovered -> {
                            onDiscoveredDevice(event.serialNumber)
                        }

                        // We clear devices when Bluetooth is off.  This isn't required, just
                        // how the example behaves.
                        is DeviceDiscoveredEvent.BluetoothOff -> {
                            deviceManager.clearDevices()
                        }

                        // When the Service notifies that devices have been cleared, we clear
                        // the UI state.
                        is DeviceDiscoveredEvent.DevicesCleared -> {
                            probes.clear()
                        }
                    }
                }
            }
        }
    }

    /**
     * Factory pattern for creating a view model that takes constructor parameters.  We dependency
     * inject the singleton instance of DeviceManager from the top-level DeviceScreen composable,
     * when getting a reference to this ViewModel.
     *
     * @property deviceManager Reference to DeviceManager.
     */
    class Factory(
        private val deviceManager: DeviceManager,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DevicesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DevicesViewModel(deviceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    /**
     * ViewModel processing of Units Button click from the Card composable on the DevicesScreen.
     *
     * @param probe UI state for the probe.
     *
     * @see ProbeState
     */
    fun toggleUnits(probe: ProbeState) {
        probe.units.value = if(probe.units.value == ProbeState.Units.FAHRENHEIT)
            ProbeState.Units.CELSIUS
        else
            ProbeState.Units.FAHRENHEIT

        Log.d(LOG_TAG, "${probe.serialNumber} units set to ${probe.units.value}")
    }

    /**
     * ViewModel processing of Bluetooth Button click from the Card composable on the DevicesScreen
     *
     * @param probe UI state for the probe
     *
     * @see ProbeState
     */
    fun toggleConnection(probe: ProbeState) {
        when(probe.connectionState.value) {
            // If the device is ADVERTISING_CONNECTABLE, then we can connect to it.  So initiate
            // the connection.
            ProbeState.ConnectionState.ADVERTISING_CONNECTABLE -> {
                deviceManager.connect(probe.serialNumber)
            }
            // If the device is CONNECTED, then we can disconnect from it.  So start the
            // disconnection.
            ProbeState.ConnectionState.CONNECTED -> {
                deviceManager.disconnect(probe.serialNumber)
            }
            // Otherwise, we cannot change the Connect State of the device.
            else -> {
                Log.d(LOG_TAG,
                    "No toggle action ${probe.serialNumber} is ${probe.connectionState.value}")
            }
        }
    }

    /**
     * ViewModel processing a color choice for a probe
     *
     * @param serial serial number of probe to update
     * @param color new color value for probe
     *
     * @see ProbeState
     * @see ProbeColor
     */
    fun setProbeColor(serial: String, color: ProbeColor) {
        deviceManager.setProbeColor(serial, color) {
            if(!it) {
                showSnackBarMessage(serial, R.string.set_color_fail)
            }
        }
    }

    /**
     * ViewModel processing a id choice for a probe
     *
     * @param serial serial number of probe to update
     * @param id new id value for probe
     *
     * @see ProbeState
     * @see ProbeID
     */
    fun setProbeID(serial: String, id: ProbeID) {
        deviceManager.setProbeID(serial, id) {
            if(!it) {
                showSnackBarMessage(serial, R.string.set_id_fail)
            }
        }
    }

    /**
     * ViewModel handler for when a probe is discovered.
     *
     * @param serialNumber Serial number of the device.
     */
    private fun onDiscoveredDevice(serialNumber: String) {

        Log.d(LOG_TAG, "Found: $serialNumber")

        // If we aren't already keeping track of the discovered probe, then add to our list.
        if(!probes.containsKey(serialNumber)) {

            // Create UI State data object for the probe and add it to our Observable list.
            probes[serialNumber] = ProbeState(serialNumber)

            // Create a coroutine in the ViewModel's scope to collect probe state changes
            // from the service.
            viewModelScope.launch {

                // Start collecting on the flow fro the probe.
                deviceManager.probeFlow(serialNumber)?.collect { probe ->

                    // Sanity check that we are keeping track of this probe.
                    probes[probe.serialNumber]?.let {

                        // Convert the state reported from the service to a form that is needed
                        // for this ViewModel/Composable screen.  The Composable will update
                        // automatically since it observing this ProbeState state object.
                        it.updateProbeState(probe, deviceManager.recordsDownloads(probe.serialNumber))

                        // The following shows how to initiate a log transfer from the device.
                        if(probe.uploadState is ProbeUploadState.ProbeUploadNeeded) {
                            deviceManager.startRecordTransfer(probe.serialNumber)
                        }
                    }
                }
            }
        }
    }

    /**
     * Create Snackbar message with given ID and String
     *
     * @param id to print next to message
     * @param resource message to print in snack bar
     */
    private fun showSnackBarMessage(id: String, resource: Int) {
        snackBarMessage.value = SnackBarMessage(id, resource)
        isSnackBarShowing.value = true
    }
}