package org.ithd.editor.navigation

import org.ithd.editor.R
import org.ithd.ui.icon.EditorIcons
import org.ithd.ui.icon.Icon
import org.ithd.ui.icon.Icon.DrawableResourceIcon
import org.ithd.editor.feature.browse.R as browseR
import org.ithd.editor.feature.settings.R as settingsR


/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    BROWSE(
        selectedIcon = DrawableResourceIcon(EditorIcons.Upcoming),
        unselectedIcon = DrawableResourceIcon(EditorIcons.UpcomingBorder),
        iconTextId = browseR.string.browse,
        titleTextId = R.string.app_name,
    ),

    //    EDITOR(
//        selectedIcon = DrawableResourceIcon(EditorIcons.Upcoming),
//        unselectedIcon = DrawableResourceIcon(EditorIcons.UpcomingBorder),
//        iconTextId = editorR.string.editor,
//        titleTextId = R.string.app_name,
//    ),
    SETTINGS(
        selectedIcon = DrawableResourceIcon(EditorIcons.Upcoming),
        unselectedIcon = DrawableResourceIcon(EditorIcons.UpcomingBorder),
        iconTextId = settingsR.string.settings,
        titleTextId = R.string.app_name,
    )
}