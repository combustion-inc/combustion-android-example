package inc.combustion.example.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import inc.combustion.LOG_TAG
import inc.combustion.example.BuildConfig
import inc.combustion.example.AppScreen
import inc.combustion.service.DeviceManager

class SettingsViewModel : ViewModel() {

    val uiState by mutableStateOf(
        SettingsScreenState(
            title = AppScreen.Settings.titleResource,
            isScanning = DeviceManager.instance.isScanningForDevices,
            onScanningToggle = {
                setScanning(it)
            },
            onDataCacheClear = {
                DeviceManager.instance.clearDevices()
            },
            versionString = "${BuildConfig.VERSION_NAME} ${BuildConfig.BUILD_TYPE}",
        )
    )

    private fun setScanning(toOn: Boolean): Boolean {
        if(toOn) {
            uiState.isScanning = DeviceManager.instance.startScanningForProbes()
        }
        else {
            uiState.isScanning = DeviceManager.instance.stopScanningForProbes()
        }

        Log.d(LOG_TAG, "Scanning Setting is ${uiState.isScanning} (requested: ${toOn})")

        return uiState.isScanning
    }
}
