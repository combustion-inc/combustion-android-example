package inc.combustion.engineering.ui.camera

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import inc.combustion.engineering.ui.CombustionAppState
import inc.combustion.engineering.ui.CombustionAppContent
import inc.combustion.engineering.ui.rememberCombustionAppState
import inc.combustion.engineering.ui.theme.CombustionIncEngineeringTheme

@Composable
fun CameraContent(
    @Suppress("UNUSED_PARAMETER") appState: CombustionAppState,
    state: CameraState
) {
    Text(
        text = "${state.title} Screen",
        color = MaterialTheme.colors.onPrimary,
        style = MaterialTheme.typography.body1
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    CombustionIncEngineeringTheme {
        val appState = rememberCombustionAppState()
        CombustionAppContent(
            appState = appState,
            content = @Composable {
                CameraScreen(appState = appState)
            }
        )
    }
}