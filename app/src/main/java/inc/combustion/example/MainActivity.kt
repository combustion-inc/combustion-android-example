package inc.combustion.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import inc.combustion.service.DeviceDiscoveredEvent
import inc.combustion.example.theme.CombustionIncEngineeringTheme
import inc.combustion.service.DeviceManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import inc.combustion.example.devices.DevicesScreen

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var _isScanning = mutableStateOf(true)
    private var _bluetoothIsOn = mutableStateOf(true)

    companion object {
        const val COMBUSTION_PERMISSIONS_REQUEST: Int = 1
        val COMBUSTION_CONSENTING_PERMISSIONS =
            // API Level 31 (Android 12) or Higher
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            }
            // Lower than API Level 31
            else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DeviceManager.initialize(application) {
            tryStartScan()
        }

        val repository = DeviceManager.instance
        repository.registerOnBoundInitialization {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    repository.discoveredProbesFlow.collect { event ->
                        when(event) {
                            is DeviceDiscoveredEvent.ScanningOn -> {
                                _isScanning.value = true
                            }
                            is DeviceDiscoveredEvent.ScanningOff -> {
                                _isScanning.value = false
                            }
                            is DeviceDiscoveredEvent.DeviceDiscovered -> {
                                _isScanning.value = true
                            }
                            is DeviceDiscoveredEvent.BluetoothOff -> {
                                _bluetoothIsOn.value = false
                            }
                            is DeviceDiscoveredEvent.BluetoothOn -> {
                                _bluetoothIsOn.value = true
                            }
                            is DeviceDiscoveredEvent.DevicesCleared -> {
                                // do nothing
                            }
                        }
                    }
                }
            }
        }

        DeviceManager.startCombustionService()
        DeviceManager.bindCombustionService()

        setContent {
            CombustionAppScreen(_isScanning, _bluetoothIsOn)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        startScanIfPermitted()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        DeviceManager.unbindCombustionService()
        super.onDestroy()
    }

    private fun startScanIfPermitted() : Boolean {
        val hasPerms = EasyPermissions.hasPermissions(
            context = this,
            perms = COMBUSTION_CONSENTING_PERMISSIONS
        )

        if(hasPerms) {
            // have the permissions we need, so start scanning for devices.
            DeviceManager.instance.startScanningForProbes()
        }

        return hasPerms
    }

    private fun tryStartScan() {
        if(!startScanIfPermitted()) {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.permissions_rationale),
                requestCode = COMBUSTION_PERMISSIONS_REQUEST,
                perms = COMBUSTION_CONSENTING_PERMISSIONS
            )
        }
    }
}

/**
 * Top-level user interface for the app.  Follows state hoisting guideline.
 *
 * [Guide: State hoisting](https://developer.android.com/jetpack/compose/state#state-hoisting)
 */
@Composable
fun CombustionAppScreen(
    isScanning: State<Boolean>,
    bluetoothIsOn: State<Boolean>
) {
    CombustionIncEngineeringTheme {

        var noDevicesReasonString = "Searching..."

        if(!bluetoothIsOn.value) {
            noDevicesReasonString = "Please Turn On Bluetooth..."
        }
        else if(!isScanning.value) {
            noDevicesReasonString = "Please Turn On Scanning..."
        }

        DevicesScreen(noDevicesReasonString)
    }
}