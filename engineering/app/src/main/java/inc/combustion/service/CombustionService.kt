package inc.combustion.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import inc.combustion.LOG_TAG
import inc.combustion.ble.DeviceScanner
import inc.combustion.log.LogManager
import inc.combustion.ble.ProbeManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CombustionService : LifecycleService() {

    private var _bluetoothIsOn = false
    private val _binder = CombustionServiceBinder()
    private val _discoveredProbesFlow = MutableSharedFlow<DeviceDiscoveredEvent>(
            FLOW_CONFIG_REPLAY, FLOW_CONFIG_BUFFER, BufferOverflow.SUSPEND
    )
    private val _probes = hashMapOf<String, ProbeManager>()

    private val periodicTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            lifecycleScope.launch {
                _probes.forEach { (_, value) ->
                    value.checkIdle()
                }
            }
        }

        override fun onFinish() {/* do nothing */ }
    }

    private val _bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        _bluetoothIsOn = false
                        emitBluetoothOffEvent()
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        DeviceScanner.stopProbeScanning()
                    }
                    BluetoothAdapter.STATE_ON -> {
                        _bluetoothIsOn = true
                        emitBluetoothOnEvent()
                        emitScanningOffEvent()
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> { }
                }
            }
        }
    }

    companion object {
        private const val FLOW_CONFIG_REPLAY = 5
        private const val FLOW_CONFIG_BUFFER = FLOW_CONFIG_REPLAY * 2

        fun start(context: Context) {
            Log.d(LOG_TAG, "Starting Combustion Android Service ...")
            Intent(context, CombustionService::class.java).also { intent ->
                context.startService(intent)
            }
        }

        fun bind(context: Context, connection: ServiceConnection) {
            Log.d(LOG_TAG, "Binding to Combustion Android Service ...")
            Intent(context, CombustionService::class.java).also { intent ->
                val flags = Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT or Context.BIND_ABOVE_CLIENT
                context.bindService(intent, connection, flags)
            }
        }

        fun stop(context: Context) {
            Log.d(LOG_TAG, "Stopping Combustion Android Service ...")
            Intent(context, CombustionService::class.java).also { intent ->
                context.stopService(intent)
            }
        }
    }

    inner class CombustionServiceBinder : Binder() {
        fun getService(): CombustionService = this@CombustionService
    }

    init {
        lifecycleScope.launch {
            // launches the block in a new coroutine every time the service is
            // in the CREATED state or above, and cancels the block when the
            // service is destroyed
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                DeviceScanner.probeAdvertisements.collect {
                    _probes.getOrPut(key = it.serialNumber) {

                        // create new probe instance
                        var newProbe =
                            ProbeManager(
                                it.mac,
                                this@CombustionService,
                                it,
                                (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter)

                        // add it to the LogManager
                        LogManager.instance.manage(this@CombustionService, newProbe)

                        // new probe discovered, so emit into the discovered probes flow
                        _discoveredProbesFlow.emit(
                            DeviceDiscoveredEvent.DeviceDiscovered(it.serialNumber)
                        )

                        newProbe
                    }.onNewAdvertisement(it)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // start periodic timer for RSSI polling
        periodicTimer.start()

        // setup receiver to see when platform Bluetooth state changes
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(_bluetoothReceiver, filter)

        // determine what the current state of Bluetooth is
        val bluetooth = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        _bluetoothIsOn = if (bluetooth.adapter == null) {
            false
        } else {
            bluetooth.adapter.isEnabled
        }

        // notify consumers on flow what the current Bluetooth state is
        if(_bluetoothIsOn) {
            emitBluetoothOnEvent()
        }
        else {
            emitBluetoothOffEvent()
        }

        Log.d(LOG_TAG, "onStartCommand ...")

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d(LOG_TAG, "onBind ...")
        return _binder
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy ...")

        // always try to unregister, even if the previous register didn't complete.
        try { unregisterReceiver(_bluetoothReceiver) } catch (e: Exception) { }

        periodicTimer.cancel()
        clearDevices()

        super.onDestroy()
    }

    val isScanningForProbes
        get() = DeviceScanner.isScanningForProbes

    val discoveredProbesFlow = _discoveredProbesFlow.asSharedFlow()

    val discoveredProbes: List<String>
        get() {
            return _probes.keys.toList()
        }

    fun startScanningForProbes(): Boolean {
        if(_bluetoothIsOn) {
            DeviceScanner.startProbeScanning(this)
        }
        if(isScanningForProbes) {
            emitScanningOnEvent()
        }
        return isScanningForProbes
    }

    fun stopScanningForProbes(): Boolean {
        if(_bluetoothIsOn) {
            DeviceScanner.stopProbeScanning()
            emitScanningOnEvent()
        }
        if(!isScanningForProbes) {
            emitScanningOffEvent()
        }

        return isScanningForProbes
    }

    fun probeFlow(serialNumber: String): SharedFlow<Probe>? =
        _probes[serialNumber]?.probeStateFlow

    fun probeState(serialNumber: String): Probe? = _probes[serialNumber]?.probe

    fun connect(serialNumber: String) = _probes[serialNumber]?.connect()

    fun disconnect(serialNumber: String) = _probes[serialNumber]?.disconnect()

    fun requestLogsFromDevice(serialNumber: String) =
        LogManager.instance.requestLogsFromDevice(this, serialNumber)

    fun exportLogsForDevice(serialNumber: String): List<LoggedProbeDataPoint>? =
        LogManager.instance.exportLogsForDevice(serialNumber)

    fun createLogFlowForDevice(serialNumber: String): Flow<LoggedProbeDataPoint> =
        LogManager.instance.createLogFlowForDevice(serialNumber)

    fun clearDevices() {
        LogManager.instance.clear()
        _probes.forEach { (_, probe) -> probe.finish() }
        _probes.clear()
        emitDevicesClearedEvent()
    }

    private fun emitBluetoothOnEvent() = _discoveredProbesFlow.tryEmit(
        DeviceDiscoveredEvent.BluetoothOn
    )

    private fun emitBluetoothOffEvent() = _discoveredProbesFlow.tryEmit(
        DeviceDiscoveredEvent.BluetoothOff
    )

    private fun emitScanningOnEvent()  = _discoveredProbesFlow.tryEmit(
        DeviceDiscoveredEvent.ScanningOn
    )

    private fun emitScanningOffEvent() = _discoveredProbesFlow.tryEmit(
        DeviceDiscoveredEvent.ScanningOff
    )

    private fun emitDevicesClearedEvent() = _discoveredProbesFlow.tryEmit(
        DeviceDiscoveredEvent.DevicesCleared
    )
}