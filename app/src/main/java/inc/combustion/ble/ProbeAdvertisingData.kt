package inc.combustion.ble

import inc.combustion.service.ProbeTemperatures

data class ProbeAdvertisingData (
    val name: String,
    val mac: String,
    val rssi: Int,
    private val advertisingData: AdvertisingData
) {
    val type = advertisingData.type
    val serialNumber = advertisingData.serialNumber
    val isConnectable = advertisingData.isConnectable
    val probeTemperatures = ProbeTemperatures.fromRawData(
        advertisingData.manufacturerData.sliceArray(AdvertisingData.TEMPERATURE_RANGE)
    )
}
