package inc.combustion.engineering.ui.share

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.combustion.LOG_TAG
import inc.combustion.service.DeviceDiscoveredEvent
import inc.combustion.engineering.google.GoogleManager
import inc.combustion.engineering.ui.AppScreen
import inc.combustion.service.DeviceManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ShareViewModel(
    private val deviceManager: DeviceManager
) : ViewModel() {

    private val _google = GoogleManager.instance

    private val _onShowMenu: () -> Unit = {
        uiState.deviceSerialNumbers.clear()
        uiState.deviceSerialNumbers.addAll(deviceManager.discoveredProbes.reversed())
    }

    private val _onDeviceSelectionChanged: (Int) -> Unit = { index ->
        uiState.selectedIndex.value = index
    }

    private val _onUploadToDrive: () -> Unit = {
        if(!_google.signedIn) {
            _google.startSignIn() {
                uploadToDrive()
            }
        } else {
            uploadToDrive()
        }
    }

    private val _onShareCsv: () -> Unit = {
        Log.d(LOG_TAG, "Share CSV ...")

        // Get device from the device repository
        /*
        val device = uiState.deviceSerialNumbers[uiState.selectedIndex.value]

        // Get logs for the device
        val logs = deviceRepository.exportLogsForDevice(device)
        csvCreator.writeCsvFile("name.csv", LoggedProbeDataPoint.csvHeader(), logs)
        */
    }

    var uiState by mutableStateOf(
        ShareScreenState(
            title = AppScreen.Settings.titleResource,
            onShowMenu = _onShowMenu,
            onDeviceSelectionChange = _onDeviceSelectionChanged,
            onUploadToDrive = _onUploadToDrive,
            onShareCsv = _onShareCsv,
        )
    )

    class Factory(private val deviceManager: DeviceManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ShareViewModel(deviceManager) as T
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

    private fun uploadToDrive() {
        // This function is called when user is signed in via Google Manager.
        // If user wasn't already signed in, this function wont be called until they successfully
        //    sign in.
        // Otherwise, this function will be called upon the button click

        Log.d(LOG_TAG, "Start Upload to Drive...")
    }
}