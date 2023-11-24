package org.ithd.editor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.ithd.editor.EditorRoute
import org.ithd.editor.EditorViewModel

const val editorNavigationRoute = "editor_route"

fun NavController.navigateToEditor(navOptions: NavOptions? = null) {
    this.navigate(editorNavigationRoute, navOptions)
}

fun NavGraphBuilder.editorScreen(
    editorViewModel: EditorViewModel,
    onBack: () -> Unit
) {
    composable(route = editorNavigationRoute) {
        EditorRoute(
            editorViewModel = editorViewModel,
            onBack = onBack
        )
    }
}