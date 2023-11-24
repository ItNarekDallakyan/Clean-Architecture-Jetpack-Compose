package org.ithd.editor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import org.ithd.browse.BrowseViewModel
import org.ithd.browse.navigation.browseNavigationRoute
import org.ithd.browse.navigation.browseScreen
import org.ithd.editor.EditorViewModel
import org.ithd.editor.ui.EditorAppState
import org.ithd.settings.navigation.settingsScreen

@Composable
fun EditorNavHost(
    appState: EditorAppState,
    modifier: Modifier = Modifier,
    startDestination: String = browseNavigationRoute,
    browseViewModel: BrowseViewModel = hiltViewModel(),
    editorViewModel: EditorViewModel = hiltViewModel(),
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        browseScreen(
            browseViewModel = browseViewModel,
            navigateEditorScreen = { videos ->
                editorViewModel.setSelectedVideos(videos)
                navController.navigateToEditor()
            }
        )
        editorScreen(
            editorViewModel = editorViewModel,
            onBack = { navController.popBackStack() }
        )
        settingsScreen()
    }
}