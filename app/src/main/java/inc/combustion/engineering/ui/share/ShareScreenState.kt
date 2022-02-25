package inc.combustion.engineering.ui.share

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class ShareScreenState(
    val title: Int,
    var deviceSerialNumbers: SnapshotStateList<String> = mutableStateListOf(),
    var selectedIndex: MutableState<Int> = mutableStateOf(0),
    val onShowMenu: () -> Unit,
    val onDeviceSelectionChange: (Int) -> Unit,
    val onUploadToDrive: () -> Unit,
    val onShareCsv: () -> Unit
)
