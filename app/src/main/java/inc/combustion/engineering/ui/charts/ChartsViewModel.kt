package inc.combustion.engineering.ui.charts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.combustion.LOG_TAG
import inc.combustion.engineering.ui.AppScreen
import inc.combustion.service.DeviceDiscoveredEvent
import inc.combustion.service.DeviceManager
import inc.combustion.service.LoggedProbeDataPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChartsViewModel(
    private val deviceManager: DeviceManager
) : ViewModel() {

    // Temporary code for testing
    private var _onPrintLogFlow: () -> Unit = {
        val device = uiState.deviceSerialNumbers[uiState.selectedIndex.value]
        val flow = deviceManager.createLogFlowForDevice(device)
        viewModelScope.launch {
            Log.d("$LOG_TAG.LogPrint", LoggedProbeDataPoint.csvHeader())
            flow.collect { log ->
                Log.d("$LOG_TAG.LogPrint", log.toCSV())
            }
        }
    }

    // Temporary code for testing
    private var _onPrintLogExport: () -> Unit = {
        val device = uiState.deviceSerialNumbers[uiState.selectedIndex.value]
        val logs = deviceManager.exportLogsForDevice(device)
        Log.d("$LOG_TAG.LogPrint", LoggedProbeDataPoint.csvHeader())
        logs?.let {
            for(log in it) {
                Log.d("$LOG_TAG.LogPrint", log.toCSV())
            }
        }
    }

    private var _onDeviceSelectionChanged: (Int) -> Unit = { index ->
        uiState.selectedIndex.value = index
    }

    private var _onShowMenu: () -> Unit = {
        uiState.deviceSerialNumbers.clear()
        uiState.deviceSerialNumbers.addAll(deviceManager.discoveredProbes.reversed())
        Log.d(LOG_TAG, "_onShowMenu: Device Count: ${uiState.deviceSerialNumbers.size}")
    }

    private var _getDataFlow: (index: Int) -> Flow<LoggedProbeDataPoint> = { index ->
        val serial = uiState.deviceSerialNumbers[index]
        deviceManager.createLogFlowForDevice(serial)
    }

    var uiState by mutableStateOf(
        ChartsScreenState(
            title = AppScreen.Charts.titleResource,
            getDataFlow =  _getDataFlow,
            onShowMenu = _onShowMenu,
            onDeviceSelectionChange = _onDeviceSelectionChanged,
            onPrintLogExport = _onPrintLogExport,
            onPrintLogFlow = _onPrintLogFlow
        )
    )

    class Factory(private val deviceManager: DeviceManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChartsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChartsViewModel(deviceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    init {
        if(deviceManager.discoveredProbes.isNotEmpty()) {
            uiState.deviceSerialNumbers.clear()
            uiState.deviceSerialNumbers.addAll(deviceManager.discoveredProbes)
        }

        viewModelScope.launch {
            deviceManager.discoveredProbesFlow.collect { event ->
                when(event) {
                    is DeviceDiscoveredEvent.ScanningOn -> {
                        uiState.deviceSerialNumbers.clear()
                        uiState.deviceSerialNumbers.addAll(deviceManager.discoveredProbes)
                    }
                    is DeviceDiscoveredEvent.BluetoothOn -> { }
                    is DeviceDiscoveredEvent.ScanningOff -> { }
                    is DeviceDiscoveredEvent.DeviceDiscovered -> {
                        uiState.deviceSerialNumbers.clear()
                        uiState.deviceSerialNumbers.addAll(deviceManager.discoveredProbes)
                    }
                    is DeviceDiscoveredEvent.BluetoothOff -> {
                        uiState.deviceSerialNumbers.clear()
                    }
                    is DeviceDiscoveredEvent.DevicesCleared -> {
                        uiState.deviceSerialNumbers.clear()
                    }
                }
            }
        }
    }

    /*
    fun dataFlow(index: Int): Flow<LoggedProbeDataPoint> {
        val serial = uiState.deviceSerialNumbers[index]
        return deviceRepository.createLogFlowForDevice(serial)
     */
        /*
        uiState.loggedProbeDataPoint = flow.collectAsState(
            initial = LoggedProbeDataPoint(
                SessionId.NULL_SESSION_ID, 0u, ProbeTemperatures(listOf(0.0)
                )
            )
        )
         */

        /*
        viewModelScope.launch {
            uiState.clearChartData.value = true
            uiState.isWaitingForData.value = true
            flow.collect { log ->
                Log.d("$LOG_TAG.LogPrint", log.toCSV())
                uiState.isWaitingForData.value = false
                uiState.temperatureData.clear()
                uiState.temperatureData.add(log.sequenceNumber.toDouble())
                uiState.temperatureData.addAll(log.temperatures.values)
            }
        }
         */
        /*
    }
         */
}