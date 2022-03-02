package inc.combustion.example.devices

import inc.combustion.example.R
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.combustion.LOG_TAG
import inc.combustion.service.DeviceDiscoveredEvent
import inc.combustion.service.ProbeUploadState
import inc.combustion.service.DeviceManager
import kotlinx.coroutines.launch
import kotlin.IllegalArgumentException
import kotlinx.coroutines.flow.*
import androidx.compose.runtime.mutableStateOf as mutableStateOf1

class DevicesViewModel(
    private val _deviceManager : DeviceManager,
) : ViewModel() {

    var probes = mutableStateMapOf<String, ProbeUiState>()
        private set

    init {
        _deviceManager.registerOnBoundInitialization {
            viewModelScope.launch {
                _deviceManager.discoveredProbesFlow.collect { event ->
                    when(event) {
                        is DeviceDiscoveredEvent.ScanningOn -> { }
                        is DeviceDiscoveredEvent.BluetoothOn -> { }
                        is DeviceDiscoveredEvent.ScanningOff -> { }
                        is DeviceDiscoveredEvent.DeviceDiscovered -> {
                            onDiscoveredDevice(event.serialNumber)
                        }
                        is DeviceDiscoveredEvent.BluetoothOff -> {
                            _deviceManager.clearDevices()
                        }
                        is DeviceDiscoveredEvent.DevicesCleared -> {
                            probes.clear()
                        }
                    }
                }
            }
        }
    }

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

    fun toggleUnits(probe: ProbeUiState) {
        probe.units.value = if(probe.units.value == ProbeUiState.Units.FAHRENHEIT)
            ProbeUiState.Units.CELSIUS
        else
            ProbeUiState.Units.FAHRENHEIT

        Log.d(LOG_TAG, "${probe.serialNumber} units set to ${probe.units.value}")
    }

    fun toggleConnection(probe: ProbeUiState) {
        when(probe.connectionState.value) {
            ProbeUiState.ConnectionState.ADVERTISING_CONNECTABLE -> _deviceManager.connect(probe.serialNumber)
            ProbeUiState.ConnectionState.CONNECTED -> _deviceManager.disconnect(probe.serialNumber)
            else -> Log.d(LOG_TAG, "No toggle action ${probe.serialNumber} is ${probe.connectionState.value}")
        }
    }

    private fun onDiscoveredDevice(serialNumber: String) {
        Log.d(LOG_TAG, "Found: $serialNumber")
        if(!probes.containsKey(serialNumber)) {
            probes[serialNumber] = ProbeUiState(serialNumber)
            viewModelScope.launch {
                _deviceManager.probeFlow(serialNumber)?.collect { probe ->
                    probes[probe.serialNumber]?.let {
                        val lastState = it.connectionState.value

                        it.updateProbeState(probe)

                        if(it.connectionState.value == ProbeUiState.ConnectionState.CONNECTED &&
                            lastState != ProbeUiState.ConnectionState.CONNECTED
                        ) {
                            logDeviceConnection(
                                "Probe",
                                probe.fwVersion ?: "TBD",
                                probe.serialNumber
                            )
                        }

                        if(probe.uploadState is ProbeUploadState.ProbeUploadNeeded) {
                            _deviceManager.startRecordTransfer(probe.serialNumber)
                        }
                    }
                }
            }
        }
    }

    private fun logDeviceConnection(deviceType: String, firmwareVersion: String, serial: String) {
        var params = Bundle()

        params.putString("device_type", deviceType)
        params.putString("serial_number", serial)
        params.putString("firmware_version", firmwareVersion)
    }

    companion object {
        fun previewData(@Suppress("UNUSED_PARAMETER") viewModel : DevicesViewModel?) : List<ProbeUiState> {
            val list = listOf(
                ProbeUiState(
                    serialNumber = "ABCDEDF",
                    macAddress = mutableStateOf1("12:34:56:78:AB:CD")
                ),
                ProbeUiState(
                    serialNumber = "1234567",
                    macAddress = mutableStateOf1("CD:EF:AB:12:34:56")
                ),
                ProbeUiState(
                    serialNumber = "CEREAL#",
                    macAddress = mutableStateOf1("11:22:33:44:55:66")
                ),
                ProbeUiState(
                    serialNumber = "QWERTY",
                    macAddress = mutableStateOf1("CO:FF:EE:15:B1:AC")
                ),
                ProbeUiState(
                    serialNumber = "10079180",
                    macAddress = mutableStateOf1("FF:EE:DD:CC:BB:AA")
                ),
                ProbeUiState(
                    serialNumber = "OIUYTT",
                    macAddress = mutableStateOf1("44:44:44:77:77:77")
                )
            )

            list[0].connectionState.value = ProbeUiState.ConnectionState.ADVERTISING_CONNECTABLE
            list[0].rssi.value = 20
            list[0].temperaturesCelsius.addAll(
                listOf(
                    80.0,
                    90.0,
                    100.0,
                    110.0,
                    120.0,
                    130.0,
                    140.0,
                    150.0
                )
            )

            list[1].connectionState.value = ProbeUiState.ConnectionState.CONNECTING
            list[1].units.value = ProbeUiState.Units.CELSIUS
            list[1].rssi.value = 100
            list[1].temperaturesCelsius.addAll(
                listOf(
                    83.0,
                    93.0,
                    103.0,
                    113.0,
                    123.0,
                    133.0,
                    143.0,
                    153.0
                )
            )

            list[2].connectionState.value = ProbeUiState.ConnectionState.CONNECTED
            list[2].units.value = ProbeUiState.Units.CELSIUS
            list[2].rssi.value = -127
            list[2].temperaturesCelsius.addAll(
                listOf(
                    81.0,
                    91.0,
                    101.0,
                    111.0,
                    121.0,
                    131.0,
                    141.0,
                    151.0
                )
            )

            list[3].connectionState.value = ProbeUiState.ConnectionState.DISCONNECTING
            list[3].rssi.value = -15
            list[3].temperaturesCelsius.addAll(
                listOf(
                    87.0,
                    97.0,
                    107.0,
                    117.0,
                    127.0,
                    137.0,
                    147.0,
                    157.0
                )
            )

            list[4].connectionState.value = ProbeUiState.ConnectionState.DISCONNECTED
            list[4].rssi.value = 127
            list[4].temperaturesCelsius.addAll(
                listOf(
                    89.0,
                    99.0,
                    109.0,
                    119.0,
                    129.0,
                    139.0,
                    149.0,
                    159.0
                )
            )

            list[5].connectionState.value = ProbeUiState.ConnectionState.OUT_OF_RANGE
            list[5].rssi.value = 90
            list[5].temperaturesCelsius.addAll(
                listOf(
                    79.0,
                    79.0,
                    119.0,
                    129.0,
                    139.0,
                    149.0,
                    159.0,
                    169.0
                )
            )

            return list
        }
    }
}