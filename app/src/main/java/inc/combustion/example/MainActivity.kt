/*
 * Project: Combustion Inc. Android Example
 * File: MainActivity.kt
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
import inc.combustion.framework.service.DeviceDiscoveredEvent
import inc.combustion.example.theme.CombustionIncEngineeringTheme
import inc.combustion.framework.service.DeviceManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import inc.combustion.example.devices.DevicesScreen

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var isScanning = mutableStateOf(true)
    private var bluetoothIsOn = mutableStateOf(true)

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

        // The following switches can be used to increase the verbosity of debug log
        // messages from the library.  We will continue to update these as needed to
        // control the output of the log.
        //  
        // DebugSettings.DEBUG_LOG_BLE_UART_IO = true
        // DebugSettings.DEBUG_LOG_BLE_OPERATIONS = true
        // DebugSettings.DEBUG_LOG_CONNECTION_STATE = true
        // DebugSettings.DEBUG_LOG_LOG_MANAGER_IO = true
        // DebugSettings.DEBUG_LOG_SESSION_STATUS = true
        // DebugSettings.DEBUG_LOG_TRANSFER = true

        // Initialize the DeviceManger
        DeviceManager.initialize(application) {
        }

        val repository = DeviceManager.instance


        // If the DeviceManager is already initialized and bound to the Combustion Android Service,
        // then the lambda registered here is immediately called.  Otherwise, this callback is
        // executed asynchronously when once bounds to the service.
        repository.registerOnBoundInitialization {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    // Collect on the discoveredProbesFlow and handle DeviceDiscoveredEvents that
                    // are produces by the service.  We process these state updates in the
                    // MainActivity to handle application wide events the affect all screens.
                    //
                    // We keep track of the Bluetooth enable/disable state and BLE scanning
                    // on/off state, so that we can inform the user.
                    //
                    // NOTE: Scanning is automatically disabled when Bluetooth is off.  And it is
                    // not automatically enabled when Bluetooth is turned on. To enable scanning
                    // across Bluetooth state changes, turn it on in response to the BluetoothOn
                    // event.  See below.
                    repository.discoveredProbesFlow.collect { event ->
                        when(event) {
                            // This will occur in response to the DeviceManager scanning API calls.
                            is DeviceDiscoveredEvent.ScanningOn -> {
                                isScanning.value = true
                            }
                            // This will occur in response to the DeviceManager scannign API calls
                            // and when Bluetooth is disabled.
                            is DeviceDiscoveredEvent.ScanningOff -> {
                                isScanning.value = false
                            }
                            // This occurs when a device is discovered during scanning.  See
                            // DevicesViewModel for an example of how to process this event.
                            is DeviceDiscoveredEvent.DeviceDiscovered -> {
                                isScanning.value = true
                            }
                            // This will occur occur upon initialization if Bluetooth is off, or when
                            // the user changes Bluetooth from On to Off.
                            is DeviceDiscoveredEvent.BluetoothOff -> {
                                bluetoothIsOn.value = false
                            }
                            // Start scanning when Bluetooth is on.  This will occur upon
                            // initialization if Bluetooth is already enabled, or when
                            // the user changes Bluetooth from Off to On.
                            is DeviceDiscoveredEvent.BluetoothOn -> {
                                bluetoothIsOn.value = true
                                tryStartScan()
                            }
                            is DeviceDiscoveredEvent.DevicesCleared -> {
                                // do nothing
                            }
                        }
                    }
                }
            }
        }

        // Start and bind to the service
        DeviceManager.startCombustionService()
        DeviceManager.bindCombustionService()

        setContent {
            CombustionAppScreen(isScanning, bluetoothIsOn)
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

    /**
     * If the user has consented to the required Bluetooth permissions, then start scanning
     * for probes.
     *
     * @return true if the user has consented to the required Bluetooth permissions.
     */
    private fun startScanIfPermitted() : Boolean {
        val hasPerms = EasyPermissions.hasPermissions(
            context = this,
            perms = COMBUSTION_CONSENTING_PERMISSIONS
        )

        if(hasPerms) {
            // We have the permissions we need, so start scanning for devices.
            DeviceManager.instance.startScanningForProbes()

            // The following call adds a simulated probe.  The simulated probe will be published
            // as a DeviceDiscoveredEvent.DeviceDiscovered event.  It produces simulated
            // temperature updates that are delivered through its state flow.  Note the serial
            // number is random and the simulated probe does not support data upload.
            //
            // DeviceManager.instance.addSimulatedProbe()
        }

        return hasPerms
    }

    /**
     * This function checks if the app has the Bluetooth permissions needed to start scanning
     * for probes.  If it does have the permissions, then scanning is started.  Otherwise, it
     * uses EasyPermissions to request the necessary permissions from the user.
     *
     * See more on how to use EasyPermissions here:
     *      https://github.com/VMadalin/easypermissions-ktx
     *
     * @see startScanIfPermitted
     * @see EasyPermissions.requestPermissions
     */
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