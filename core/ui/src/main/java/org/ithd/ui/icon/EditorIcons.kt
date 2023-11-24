package org.ithd.ui.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.ReplayCircleFilled
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import org.ithd.editor.core.core.ui.R

/**
 * Ithd-editor icons. Material icons are [ImageVector]s, custom icons are drawable resource IDs.
 */
object EditorIcons {
    val PlayIcon = Icons.Default.PlayCircleOutline
    val PauseIcon = Icons.Default.PauseCircleOutline
    val ReplayIcon = Icons.Default.Replay
    val FullScreenIcon = Icons.Default.Fullscreen
    val FullScreenExitIcon = Icons.Default.FullscreenExit
    val Upcoming = R.drawable.ic_upcoming
    val UpcomingBorder = R.drawable.ic_upcoming_border
    val AddIcon = Icons.Default.Add
    val Edit = Icons.Outlined.Edit
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}