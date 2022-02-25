package inc.combustion.example.settings

data class SettingsScreenState(
    val title: Int,
    var isScanning: Boolean,
    val onScanningToggle: (Boolean) -> Boolean,
    var onDataCacheClear: () -> Unit,
    val versionString: String,
)