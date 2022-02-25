package inc.combustion.engineering.ui.settings

import androidx.compose.runtime.MutableState

data class SettingsScreenState(
    val title: Int,
    var isScanning: Boolean,
    val onScanningToggle: (Boolean) -> Boolean,
    var onDataCacheClear: () -> Unit,
    var accountEmail: MutableState<String>,
    val onAccountClick: () -> Unit,
    val versionString: String,
)