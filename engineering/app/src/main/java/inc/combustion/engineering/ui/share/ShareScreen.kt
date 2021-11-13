package inc.combustion.engineering.ui.share

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import inc.combustion.engineering.R
import inc.combustion.engineering.ui.CombustionAppContent
import inc.combustion.engineering.ui.CombustionAppState
import inc.combustion.engineering.ui.components.ExposedDropdownMenu
import inc.combustion.engineering.ui.rememberCombustionAppState
import inc.combustion.engineering.ui.theme.CombustionIncEngineeringTheme
import inc.combustion.service.DeviceManager

@Composable
fun ShareScreen(
    appState: CombustionAppState
) {
    val viewModel: ShareViewModel = viewModel(factory = ShareViewModel.Factory(DeviceManager.instance))
    ShareContent(
        appState,
        remember {
            viewModel.uiState
        }
    )
}

@Composable
fun ShareContent(
    appState: CombustionAppState,
    state: ShareScreenState
) {

    Column {
        if(state.deviceSerialNumbers.size > 0) {
            ExposedDropdownMenu(
                labelText = "ProbeManager",
                items = state.deviceSerialNumbers,
                selectedIndex = state.selectedIndex.value,
                onItemSelected = state.onDeviceSelectionChange,
                onShowMenu = state.onShowMenu
            )
            Spacer(
                modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding))
            )
            Row() {
                Button(
                    onClick = { state.onUploadToDrive() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                    modifier = Modifier.weight(0.5f),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_to_drive_24),
                            contentDescription = "Upload to Drive",
                        )
                        Text(text = "Upload to Drive")
                    }
                }

                Button(
                    onClick = { state.onShareCsv() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                    modifier = Modifier.weight(0.5f),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_share_24),
                            contentDescription = "Share CSV",
                        )
                        Text(text = "Share CSV")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    color = MaterialTheme.colors.onPrimary,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.large_padding),
                            vertical = dimensionResource(id = R.dimen.small_padding)
                        ),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.subtitle2,
                    text = appState.noDevicesReasonString
                )
            }
        }
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
                ShareScreen(appState = appState)
            }
        )
    }
}
