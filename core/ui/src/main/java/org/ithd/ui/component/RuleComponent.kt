package org.ithd.ui.component

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RuleComponent(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier.width(1.dp),
        color = MaterialTheme.colorScheme.primary
    )
}