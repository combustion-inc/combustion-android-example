package inc.combustion.engineering.ui.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import inc.combustion.LOG_TAG
import inc.combustion.engineering.BuildConfig
import inc.combustion.engineering.google.GoogleManager
import inc.combustion.engineering.ui.AppScreen
import inc.combustion.service.DeviceManager

class SettingsViewModel : ViewModel() {

    private val _google = GoogleManager.instance

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
            accountEmail = mutableStateOf(""),
            onAccountClick = {
                onAccountClick()
            },
            versionString = "${BuildConfig.VERSION_NAME} ${BuildConfig.BUILD_TYPE}",
        )
    )

    init {
        uiState.accountEmail.value = if(_google.signedIn) _google.email ?: "" else ""

        // if we initiate sign in from this screen,then update view on
        _google.registerSignInSuccessHandler {
            uiState.accountEmail.value = _google.email ?: ""
            Log.d(LOG_TAG, "Sign in complete: ${_google.signedIn} ${uiState.accountEmail.value}")
        }

        Log.d(LOG_TAG, "View Model Created")
    }

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

    private fun onAccountClick() {
        if(_google.signedIn) {
            _google.signOut()
            uiState.accountEmail.value = ""
        }
        else {
            _google.startSignIn()
        }
    }
}
