package inc.combustion.example.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.example.CombustionAppState
import inc.combustion.service.DeviceManager

data class DevicesScreenState(
    val probes: SnapshotStateMap<String, ProbeUiState>,
    val isSnackBarShowing: MutableState<Boolean>,
    val snackBarMessage: MutableState<DevicesViewModel.SnackBarMessage>,
    val onDismissSnackbarMessage: () -> Unit,
    val onUnitsClick: (ProbeUiState) -> Unit,
    val onBluetoothClick: (ProbeUiState) -> Unit
)

@Composable
fun DevicesScreen(
    appState: CombustionAppState
) {
    val viewModel : DevicesViewModel = viewModel(
        factory = DevicesViewModel.Factory(DeviceManager.instance, appState.firebaseAnalytics)
    )
    val screenState = DevicesScreenState(
        probes = remember { viewModel.probes },
        isSnackBarShowing = remember { viewModel.isSnackBarShowing },
        snackBarMessage = remember { viewModel.snackBarMessage },
        onDismissSnackbarMessage = {
           viewModel.dismissSnackBarMessage()
        },
        onUnitsClick = { device ->
            viewModel.toggleUnits(device)
        }
    ) { device ->
        viewModel.toggleConnection(device)
    }

    DevicesContent(
        appState = appState,
        screenState = screenState
    )
}
