package inc.combustion.log

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import inc.combustion.LOG_TAG
import inc.combustion.service.DeviceConnectionState
import inc.combustion.ble.ProbeManager
import inc.combustion.service.DebugSettings
import inc.combustion.service.LoggedProbeDataPoint
import inc.combustion.service.ProbeUploadState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class LogManager {
    private val _probes = hashMapOf<String, ProbeManager>()
    private val _temperatureLogs:  SortedMap<String, ProbeTemperatureLog> = sortedMapOf()

    companion object {
        private lateinit var INSTANCE: LogManager
        private val initialized = AtomicBoolean(false)

        val instance: LogManager
            get() {
            if(!initialized.getAndSet(true)) {
                INSTANCE = LogManager()
            }
            return INSTANCE
        }
    }

    fun manage(owner: LifecycleOwner, probeManager: ProbeManager) {
        if(!_probes.containsKey(probeManager.probe.serialNumber)) {
            // add probeManager
            _probes[probeManager.probe.serialNumber] = probeManager

            // create new temperature log for probeManager and track it
            if(!_temperatureLogs.containsKey(probeManager.probe.serialNumber)) {
                _temperatureLogs[probeManager.probe.serialNumber] =
                    ProbeTemperatureLog(probeManager.probe.serialNumber)
            }

            // maintain reference to log for further processing
            val temperatureLog = _temperatureLogs.getValue(probeManager.probe.serialNumber)

            // monitor this probes state flow
            probeManager.addJob(owner.lifecycleScope.launch {
                owner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    probeManager.probeStateFlow.collect {

                        // if device is disconnected, uploading is unavailable, so update
                        // the state if it hs not already been updated and let the log know
                        // to expect another log request at some point in the future, for its
                        // internal bookkeeping.
                        if(it.connectionState != DeviceConnectionState.CONNECTED
                            && probeManager.probe.uploadState != ProbeUploadState.Unavailable) {
                            probeManager.onNewUploadState(ProbeUploadState.Unavailable)
                            temperatureLog?.expectFutureLogRequest()
                        }
                    }
                }
            })
            // monitor this probes device status flow
            probeManager.addJob(owner.lifecycleScope.launch {
                owner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    probeManager.deviceStatusFlow.collect { deviceStatus ->

                        when(probeManager.probe.uploadState) {
                            // if upload state is currently unavailable, and we now have a
                            // DeviceStatus, then we have everything we need to start an upload.
                            //
                            // event the new upload state to the client, and they can initiate
                            // the record transfer when they are ready.
                            ProbeUploadState.Unavailable -> {
                                probeManager.onNewUploadState(ProbeUploadState.ProbeUploadNeeded)
                            }
                            ProbeUploadState.ProbeUploadNeeded -> {
                                // do nothing, wait for the client to request the transfer
                            }
                            is ProbeUploadState.ProbeUploadInProgress -> {
                                // add device status data points to the log while the upload
                                // is in progress.  don't event the status, because we will
                                // do that below in processing the log response.
                                temperatureLog.addFromDeviceStatus(deviceStatus)

                                // not receiving expected log responses), then complete the log
                                // request and transition state.
                                if(temperatureLog.logRequestIsStalled) {
                                    val sessionStatus = temperatureLog.completeLogRequest()
                                    probeManager.onNewUploadState(sessionStatus.toProbeUploadState())
                                }
                            }
                            is ProbeUploadState.ProbeUploadComplete -> {
                                if(DebugSettings.DEBUG_LOG_LOG_MANAGER_IO) {
                                    Log.d(
                                        LOG_TAG, "STATUS-RX : " +
                                                "${deviceStatus.maxSequenceNumber}"
                                    )
                                }

                                // add the device status to the temperature log
                                val sessionStatus = temperatureLog.addFromDeviceStatus(deviceStatus)

                                // if we have any dropped records that initiate a log request to
                                // backfill from the missing records.
                                if(sessionStatus.droppedRecords.isNotEmpty()) {
                                    val drop = sessionStatus.droppedRecords.first()
                                    val range = RecordRange(drop, drop)
                                    startBackfillLogRequest(
                                        owner, temperatureLog, probeManager, range, deviceStatus.maxSequenceNumber)
                                }
                                // we've synchronized the log on the device here, now maintain
                                // the log with the data points from the device status message.
                                // and report out the progress as part of the complete state.
                                else {
                                    probeManager.onNewUploadState(sessionStatus.toProbeUploadState())
                                }
                            }
                        }
                    }
                }
            })
            // monitor this probes log response flow
            probeManager.addJob(owner.lifecycleScope.launch {
                owner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    probeManager.logResponseFlow.collect { response ->

                        // debug log BLE IO if enabled.
                        if(DebugSettings.DEBUG_LOG_LOG_MANAGER_IO) {
                            Log.d(LOG_TAG, "LOG-RX    : ${response.sequenceNumber}")
                        }

                        // add the response to the temperature log and handle upload progress
                        val uploadProgress = temperatureLog.addFromLogResponse(response)

                        // if we aren't complete, then update the stats of the uploading state
                        if(!uploadProgress.isComplete) {
                            probeManager.onNewUploadState(uploadProgress.toProbeUploadState())
                        }
                        // otherwise complete the request, and change the state to complete
                        else {
                            val sessionStatus = temperatureLog.completeLogRequest()
                            probeManager.onNewUploadState(sessionStatus.toProbeUploadState())
                        }
                    }
                }
            })
        }
    }

    fun clear() {
        _probes.clear()
        _temperatureLogs.clear()
    }

    fun requestLogsFromDevice(owner: LifecycleOwner, serialNumber: String) {
        val device = _probes[serialNumber]
        val temperatureLog = _temperatureLogs[serialNumber]

        device?.let { probe ->
            temperatureLog?.let { log ->

                // prepare log for the request and determine the needed range
                val range =  log.prepareForLogRequest(
                    probe.probe.minSequence,
                    probe.probe.maxSequence
                )

                // if for some reason there isn't anything to request,
                // log a message and return
                if(range.size == 0u)  {
                    if(DebugSettings.DEBUG_LOG_TRANSFER) {
                        Log.w(LOG_TAG, "No need to request logs from device")
                    }
                    return
                }

                // initialize the start of the log request with the temperature log
                val progress = log.startLogRequest(range)

                // update the probe's upload state with the progress.
                owner.lifecycleScope.launch {
                    probe.onNewUploadState(progress.toProbeUploadState())
                }

                // send the request to the device to start the upload
                probe.sendLogRequest(owner, range.minSeq, range.maxSeq)

                // processing the resulting LogRequest flow happens in the coroutine above.
            }
        }
    }

    fun exportLogsForDevice(serialNumber: String): List<LoggedProbeDataPoint>? {
        return _temperatureLogs[serialNumber]?.dataPoints
    }

    fun createLogFlowForDevice(serialNumber: String): Flow<LoggedProbeDataPoint> {
        return flow {
            _probes[serialNumber]?.let { probe ->

                // if the device isn't uploading or hasn't completed an upload, then
                // return out of this flow immediately, there is nothing to do.  the device
                // needs to connect, or the client needs to initiate the record transfer.
                if(probe.probe.uploadState !is ProbeUploadState.ProbeUploadInProgress &&
                        probe.probe.uploadState !is ProbeUploadState.ProbeUploadComplete)  {
                   return@flow
                }

                // wait for the upload to complete before emitting into the flow.
                while(probe.probe.uploadState is ProbeUploadState.ProbeUploadInProgress &&
                        currentCoroutineContext().isActive) {
                    delay(250)
                }

                // upload is complete, so get the logged data points stored on the phone and emit
                // them into the flow, in order.
                val log = _temperatureLogs[serialNumber]
                log?.let { temperatureLog ->
                    temperatureLog.dataPoints.forEach {
                        emit(it)
                    }

                    // collect the probe status updates as they come in, in order, and emit them
                    // into the flow for the consumer.  if we are no longer in an upload complete
                    // syncing state, then exit out of this flow.
                    val sessionId = log.currentSessionId
                    probe.deviceStatusFlow.collect { deviceStatus ->

                        if(probe.probe.uploadState !is ProbeUploadState.ProbeUploadInProgress &&
                            probe.probe.uploadState !is ProbeUploadState.ProbeUploadComplete)  {
                                throw CancellationException("Upload State is Now Invalid")
                        }

                        emit(LoggedProbeDataPoint.fromDeviceStatus(sessionId, deviceStatus))
                    }
                }
            }
        }
    }

    private fun startBackfillLogRequest(
        owner: LifecycleOwner,
        log: ProbeTemperatureLog,
        probeManager: ProbeManager,
        range: RecordRange,
        currentDeviceStatusSequence: UInt
    ) {

        // if for some reason there isn't anything to request,
        // log a message and return
        if(range.size == 0u)  {
            if(DebugSettings.DEBUG_LOG_TRANSFER) {
                Log.w(LOG_TAG, "No need to backfill request logs from device $range")
            }
            return
        }

        // initialize the start of the log request with the temperature log
        val progress = log.startLogBackfillRequest(range, currentDeviceStatusSequence)

        // update the probeManager's upload state with the progress.
        owner.lifecycleScope.launch {
            probeManager.onNewUploadState(progress.toProbeUploadState())
        }

        // send the request to the device to start the upload
        probeManager.sendLogRequest(owner, range.minSeq, range.maxSeq)

        // processing the resulting LogRequest flow happens in the coroutine above.
    }
}