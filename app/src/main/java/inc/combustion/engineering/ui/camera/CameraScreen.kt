package inc.combustion.engineering.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import inc.combustion.engineering.ui.AppScreen
import inc.combustion.engineering.ui.CombustionAppState

class CameraState(
    val title: String
) {

}

@Composable
fun rememberCameraState(
    title: String = stringResource(AppScreen.Camera.titleResource)
) = remember(title) {
   CameraState(title)
}

@Composable
fun CameraScreen(
    appState: CombustionAppState
) {
    val screenState = rememberCameraState()
    CameraContent(appState, screenState)
}