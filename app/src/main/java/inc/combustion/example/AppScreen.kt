package inc.combustion.example

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import inc.combustion.example.R

/**
 * Enumerates the screens in the app
 *
 * @property route Navigation route
 * @property titleResource Resource ID for screen's title
 * @property iconResource Resource ID for screen's icon
 */
sealed class AppScreen(val route: String, @StringRes val titleResource: Int, @DrawableRes val iconResource: Int) {
    object Devices : AppScreen(route = "DevicesScreen", titleResource = R.string.devices_screen_title, iconResource = R.drawable.ic_flame_24)
    object Charts : AppScreen(route = "ChartsScreen", titleResource = R.string.charts_screen_title, iconResource = R.drawable.ic_chart_24)
    object Camera : AppScreen(route = "CameraScreen", titleResource = R.string.camera_screen_title, iconResource = R.drawable.ic_camera_24)
    object Share : AppScreen(route = "ShareScreen", titleResource = R.string.share_screen_title, iconResource = R.drawable.ic_add_to_drive_24)
    object Settings : AppScreen(route = "SettingsScreen", titleResource = R.string.settings_screen_title, iconResource = R.drawable.ic_settings_24)

    /**
     * @return Screen's icon
     */
    @Composable
    fun getImageVector() : ImageVector {
        return ImageVector.vectorResource(iconResource)
    }

    /**
     * @return Screen's title
     */
    @Composable
    fun getTitle() : String {
        return stringResource(titleResource)
    }
}