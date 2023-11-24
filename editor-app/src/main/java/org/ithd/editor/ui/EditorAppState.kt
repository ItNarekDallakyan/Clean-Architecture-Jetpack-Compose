package org.ithd.editor.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import org.ithd.browse.navigation.browseNavigationRoute
import org.ithd.browse.navigation.navigateToBrowse
import org.ithd.editor.navigation.TopLevelDestination
import org.ithd.editor.navigation.editorNavigationRoute
import org.ithd.settings.navigation.navigateToSettings
import org.ithd.settings.navigation.settingsNavigationRoute

@Composable
fun rememberEditorAppState(
    windowsSizeClass: WindowSizeClass,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): EditorAppState {
    return remember(
        windowsSizeClass,
        coroutineScope,
        navController
    ) {
        EditorAppState(
            navController,
            coroutineScope,
            windowsSizeClass
        )
    }
}

@Stable
class EditorAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowsSizeClass: WindowSizeClass
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination


    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() =
            when (currentDestination?.route) {
                browseNavigationRoute -> TopLevelDestination.BROWSE
                // editorNavigationRoute -> TopLevelDestination.EDITOR
                settingsNavigationRoute -> TopLevelDestination.SETTINGS
                else -> null
            }
    val shouldShowBottomBar: Boolean
        @Composable get() = this.currentTopLevelDestination != null

    val shouldShowStatusBar: Boolean
        @Composable get() = this.currentDestination?.route == editorNavigationRoute

    /**
     * Map of top level destinations to be used in the BottomBar. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }


            when (topLevelDestination) {
                TopLevelDestination.BROWSE -> navController.navigateToBrowse(topLevelNavOptions)
                // TopLevelDestination.EDITOR -> navController.navigateToEditor(topLevelNavOptions)
                TopLevelDestination.SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            }
        }
    }
}