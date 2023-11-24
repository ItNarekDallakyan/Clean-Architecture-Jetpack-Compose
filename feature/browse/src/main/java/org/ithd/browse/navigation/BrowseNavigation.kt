package org.ithd.browse.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.ithd.browse.BrowseRoute
import org.ithd.browse.BrowseViewModel

const val browseNavigationRoute = "browse_route"

fun NavController.navigateToBrowse(navOptions: NavOptions? = null) {
    this.navigate(browseNavigationRoute, navOptions)
}

fun NavGraphBuilder.browseScreen(
    browseViewModel: BrowseViewModel,
    navigateEditorScreen: (MutableList<Uri>) -> Unit
) {
    composable(route = browseNavigationRoute) {
        BrowseRoute(
            browseViewModel = browseViewModel,
            navigateEditorScreen = navigateEditorScreen
        )
    }
}