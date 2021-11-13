package inc.combustion.engineering.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.storage.base.SettingValueState
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import inc.combustion.engineering.R
import inc.combustion.engineering.ui.CombustionAppContent
import inc.combustion.engineering.ui.CombustionAppState
import inc.combustion.engineering.ui.rememberCombustionAppState
import inc.combustion.engineering.ui.theme.CombustionIncEngineeringTheme

@Composable
fun SettingsScreen(
    appState: CombustionAppState
) {
    val viewModel: SettingsViewModel = viewModel()
    SettingsContent(
        appState,
        remember {
            viewModel.uiState
        }
    )
}

@Composable
fun SettingsContent(
    @Suppress("UNUSED_PARAMETER") appState: CombustionAppState,
    state: SettingsScreenState,
) {
    val checkedState: SettingValueState<Boolean> = rememberBooleanSettingState()
    val accountEmail: MutableState<String> = remember{ state.accountEmail }
    val versionString: String = remember{ state.versionString }

    checkedState.value = state.isScanning
    accountEmail.value = state.accountEmail.value

    Column {
        val scanningSubTitle = if(checkedState.value) "Searching for probes" else "Not searching for probes"
        val emailSubTitle = if(accountEmail.value == "") "Sign in" else accountEmail.value

        Divider()
        SettingsSwitch(
            icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = "BLE Scanning") },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "BLE Scanning") },
            subtitle = { Text(text = scanningSubTitle) },
            state = checkedState,
            onCheckedChange = {
                checkedState.value = state.onScanningToggle(it)
            }
        )
        Divider()
        SettingsMenuLink(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flame_24),
                    contentDescription = "Data Cache"
                )
           },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "Data Cache") },
            subtitle = { Text(text = "Clear device list and uploaded data") },
            onClick = {
                state.onDataCacheClear()
            }
        )
        Divider()
        SettingsMenuLink(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account_box_24),
                    contentDescription = "Google Account"
                )
            },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "Google Account") },
            subtitle = { Text(text = emailSubTitle) },
            onClick = {
                state.onAccountClick()
            }
        )
        Divider()
        SettingsMenuLink(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right_24),
                    contentDescription = "Version String"
                )
            },
            modifier = Modifier.background(MaterialTheme.colors.background),
            title = { Text(text = "App Version") },
            subtitle = { Text(text = versionString) },
            onClick = {
                // do nothing
            }
        )
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    CombustionIncEngineeringTheme {
        val appState = rememberCombustionAppState()
        CombustionAppContent(
            appState = appState,
            content = @Composable {
                Column {
                    SettingsSwitch(
                        icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = "Play") },
                        title = { Text(text = "Hello") },
                        subtitle = { Text(text = "This is a longer text") },
                        onCheckedChange = {},
                    )
                }
            }
        )
    }
}
