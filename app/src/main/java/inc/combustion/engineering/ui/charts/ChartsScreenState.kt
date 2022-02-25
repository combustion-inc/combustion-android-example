package inc.combustion.engineering.ui.charts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import inc.combustion.service.LoggedProbeDataPoint
import kotlinx.coroutines.flow.Flow

data class ChartsScreenState(
    val title: Int,
    var deviceSerialNumbers: SnapshotStateList<String> = mutableStateListOf(),
    var selectedIndex: MutableState<Int> = mutableStateOf(-1),
    var clearChartData: MutableState<Boolean> = mutableStateOf(false),
    val getDataFlow: (Int) -> Flow<LoggedProbeDataPoint>,
    val onShowMenu: () -> Unit,
    val onDeviceSelectionChange: (Int) -> Unit,
    val onPrintLogExport: () -> Unit,
    val onPrintLogFlow: () -> Unit,
)
