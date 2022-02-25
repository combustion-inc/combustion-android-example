package inc.combustion.example.devices

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import inc.combustion.service.DeviceConnectionState
import inc.combustion.service.ProbeUploadState
import inc.combustion.service.Probe

data class ProbeUiState(
    val serialNumber: String,

    var macAddress: MutableState<String> = mutableStateOf("TBD"),
    var firmwareVersion: MutableState<String?> = mutableStateOf(null),
    var rssi: MutableState<Int> = mutableStateOf(0),
    var temperaturesCelsius: SnapshotStateList<Double> = mutableStateListOf(
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0
    ),
    var connectionState: MutableState<ConnectionState> = mutableStateOf(ConnectionState.OUT_OF_RANGE),
    var units: MutableState<Units> = mutableStateOf(Units.FAHRENHEIT),
    var recordsTransferred: MutableState<UInt> = mutableStateOf(0u),
    var recordsRequested: MutableState<UInt> = mutableStateOf(0u),
    var uploadProgress: MutableState<Float> = mutableStateOf(0.0f),
    var uploadState: MutableState<UploadState> = mutableStateOf(UploadState.NEEDED),
) {
    enum class Units(val string: String) {
        FAHRENHEIT("Fahrenheit"),
        CELSIUS("Celsius")
    }

    enum class ConnectionState(val string: String) {
        OUT_OF_RANGE("Out of Range"),
        ADVERTISING_CONNECTABLE("Advertising Connectable"),
        ADVERTISING_NOT_CONNECTABLE("Advertising Not Connectable"),
        CONNECTING("Connecting"),
        CONNECTED("Connected"),
        DISCONNECTING("Disconnecting"),
        DISCONNECTED("Disconnected");

        companion object {
            fun fromDeviceConnectionState(state: DeviceConnectionState) : ConnectionState {
                return when(state) {
                    DeviceConnectionState.OUT_OF_RANGE -> OUT_OF_RANGE
                    DeviceConnectionState.ADVERTISING_CONNECTABLE -> ADVERTISING_CONNECTABLE
                    DeviceConnectionState.ADVERTISING_NOT_CONNECTABLE -> ADVERTISING_NOT_CONNECTABLE
                    DeviceConnectionState.CONNECTING -> CONNECTING
                    DeviceConnectionState.CONNECTED -> CONNECTED
                    DeviceConnectionState.DISCONNECTING -> DISCONNECTING
                    DeviceConnectionState.DISCONNECTED -> DISCONNECTED
                }
            }
        }
    }

    enum class UploadState {
        IN_PROGRESS,
        COMPLETE,
        NEEDED
    }

    val T1 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(0)))

    val T2 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(1)))

    val T3 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(2)))

    val T4 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(3)))

    val T5 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(4)))

    val T6 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(5)))

    val T7 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(6)))

    val T8 : MutableState<String>
        get() = mutableStateOf(String.format("%.1f", getTemperature(7)))

    fun updateProbeState(state: Probe) {
        macAddress.value = state.mac
        firmwareVersion.value = state.fwVersion
        connectionState.value = ConnectionState.fromDeviceConnectionState(state.connectionState)
        rssi.value = state.rssi
        temperaturesCelsius.clear()
        temperaturesCelsius.addAll(state.temperatures.values)

        if(state.uploadState is ProbeUploadState.ProbeUploadInProgress) {
            uploadProgress.value =
                state.uploadState.recordsTransferred.toFloat() /
                        state.uploadState.recordsRequested.toFloat()
            recordsTransferred.value = state.uploadState.recordsTransferred
            recordsRequested.value = state.uploadState.recordsRequested
            uploadState.value = UploadState.IN_PROGRESS
        }
        else if(state.uploadState is ProbeUploadState.ProbeUploadComplete){
            uploadProgress.value = 1.0f
            recordsTransferred.value = 0u
            recordsRequested.value = 0u
            uploadState.value = UploadState.COMPLETE
        }
        else {
            uploadProgress.value = 0.0f
            recordsTransferred.value = 0u
            recordsRequested.value = 0u
            uploadState.value = UploadState.NEEDED
        }
    }

    private fun getTemperature(index: Int) : Double {
        return if(units.value == Units.CELSIUS)
            temperaturesCelsius[index]
        else
            (temperaturesCelsius[index] * 1.8) + 32.0
    }
}