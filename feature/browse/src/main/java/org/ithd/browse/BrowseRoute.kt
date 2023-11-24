package org.ithd.browse

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.ithd.editor.feature.browse.R
import org.ithd.ui.icon.EditorIcons.Edit
import org.ithd.ui.theme.EditorTypography

@Composable
internal fun BrowseRoute(
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel,
    navigateEditorScreen: (MutableList<Uri>) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { data ->
        if (data.isNotEmpty()) {
            navigateEditorScreen(data.toMutableList())
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                galleryLauncher.launch("video/*")
            }
        ) {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = Edit,
                    contentDescription = "browse_compose_icon"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.compose),
                    style = EditorTypography.bodyLarge
                )
            }
        }
    }
}