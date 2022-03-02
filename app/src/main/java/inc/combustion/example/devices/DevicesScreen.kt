package inc.combustion.example.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.service.DeviceManager

data class DevicesScreenState(
    val probes: SnapshotStateMap<String, ProbeUiState>,
    val onUnitsClick: (ProbeUiState) -> Unit,
    val onBluetoothClick: (ProbeUiState) -> Unit
)

@Composable
fun DevicesScreen(
    noDevicesReasonString: String
) {
    val viewModel : DevicesViewModel = viewModel(
        factory = DevicesViewModel.Factory(DeviceManager.instance)
    )
    val screenState = DevicesScreenState(
        probes = remember { viewModel.probes },
        onUnitsClick = { device ->
            viewModel.toggleUnits(device)
        }
    ) { device ->
        viewModel.toggleConnection(device)
    }

    DevicesContent(
        noDevicesReasonString = noDevicesReasonString,
        screenState = screenState
    )
}
