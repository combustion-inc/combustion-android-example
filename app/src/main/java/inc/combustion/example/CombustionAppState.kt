package inc.combustion.example

import android.content.res.Resources
import android.os.Bundle
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import inc.combustion.example.charts.ChartsScreen
import inc.combustion.example.devices.DevicesScreen
import inc.combustion.example.settings.SettingsScreen
import inc.combustion.example.share.ShareScreen

/**
 * Manages top-level activity state, user interface logic and navigation.
 *
 * @property scaffoldState remembered state
 * @property navController remembered state
 * @property topNavigationScreen screen accessible from top bar
 * @property bottomNavigationScreens screens accessible from bottom bar
 * @property startScreen screen shown on launch.
 * @property resources access to resources
 */
class CombustionAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    val topNavigationScreen: AppScreen,
    val bottomNavigationScreens: List<AppScreen>,
    val startScreen: AppScreen,
    val firebaseAnalytics: FirebaseAnalytics,
    private val resources: Resources
) {
    /**
     * Global reason that no devices are available (see MainActivity).
     */
    var noDevicesReasonString = ""

    /**
     * Setup UI logic for the navigation graph.
     *
     * @param modifier any modifiers for the underlying NavHost
     * @return navigation graph composable function
     */
    @Composable
    fun NavHost(modifier: Modifier) {
        androidx.navigation.compose.NavHost(
            navController,
            startDestination = startScreen.route,
            modifier
        ) {
            val appState = this@CombustionAppState
            composable(AppScreen.Settings.route) { SettingsScreen(appState) }
            for (screen in bottomNavigationScreens) {
                composable(screen.route) {
                    when (screen) {
                        is AppScreen.Devices -> DevicesScreen(appState)
                        is AppScreen.Charts -> ChartsScreen(appState)
                        is AppScreen.Share -> ShareScreen(appState)
                        is AppScreen.Settings -> SettingsScreen(appState)
                    }
                }
            }
        }
    }

    /**
     * UI logic for top bar navigation click handling.
     *
     * @return lambda for handling top bar click.
     */
    fun onTopBarNavigationClick(): () -> Unit {
        return {
            // do nothing at this point.  maybe add drawer in future.
        }
    }

    /**
     * UI logic for top bar action button click handling
     *
     * @return lambda for handling top bar action click.
     */
    fun onTopBarActionClick(): () -> Unit {
        return {
            // navigate to the composable.
            onNavigate(topNavigationScreen.route)
        }
    }

    /**
     * UI logic for bottom bar navigation button click handling
     *
     * @param screen AppScreen that was clicked.
     * @return lambda for handling the click
     */
    fun onBottomBarNavigateClick(screen: AppScreen): () -> Unit {
        return {
            // navigate to the composable.
            onNavigate(screen.route)
        }
    }

    /**
     * Navigation graph logic and backstack management.
     *
     * [Guide: Bottom navigation](https://developer.android.com/jetpack/compose/navigation#bottom-nav)
     *
     * @param route the destination route
     */
    private fun onNavigate(route: String) {
        logNavigation(route)
        navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }

    private fun logNavigation(route: String) {
        var params = Bundle()

        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, route)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, route)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }
}

@Composable
fun rememberCombustionAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    topNavigationScreen: AppScreen = AppScreen.Settings,
    bottomNavigationScreens: List<AppScreen> = listOf(
        AppScreen.Devices,
        AppScreen.Charts,
        AppScreen.Share
    ),
    startScreen: AppScreen = AppScreen.Devices,
    resources: Resources = LocalContext.current.resources,
    firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(LocalContext.current)
) = remember(
    scaffoldState,
    navController,
    topNavigationScreen,
    bottomNavigationScreens,
    startScreen,
    resources,
    firebaseAnalytics
) {
    CombustionAppState(
        scaffoldState = scaffoldState,
        navController = navController,
        topNavigationScreen = topNavigationScreen,
        bottomNavigationScreens = bottomNavigationScreens,
        startScreen = startScreen,
        resources = resources,
        firebaseAnalytics = firebaseAnalytics
    )
}