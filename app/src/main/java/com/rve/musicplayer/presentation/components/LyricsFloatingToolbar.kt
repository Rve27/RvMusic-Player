package com.rve.musicplayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LyricsFloatingToolbar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    showSyncedLyrics: Boolean?,
    onShowSyncedLyricsChange: (Boolean) -> Unit,
    hasSyncedLyrics: Boolean,
    onMoreClick: () -> Unit,
    backgroundColor: Color,
    onBackgroundColor: Color,
    accentColor: Color,
    onAccentColor: Color
) {
    if (showSyncedLyrics == null) return

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = backgroundColor,
                contentColor = onBackgroundColor
            ),
            onClick = onNavigateBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = onBackgroundColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToggleSegmentButton(
                modifier = Modifier.weight(1f).height(50.dp),
                active = showSyncedLyrics,
                enabled = hasSyncedLyrics,
                activeColor = accentColor,
                inactiveColor = backgroundColor,
                activeContentColor = onAccentColor,
                inactiveContentColor = onBackgroundColor,
                activeCornerRadius = 50.dp,
                onClick = { onShowSyncedLyricsChange(true) },
                text = "Synced"
            )

            ToggleSegmentButton(
                modifier = Modifier.weight(1f).height(50.dp),
                active = !showSyncedLyrics,
                enabled = true,
                activeColor = accentColor,
                inactiveColor = backgroundColor,
                activeContentColor = onAccentColor,
                inactiveContentColor = onBackgroundColor,
                activeCornerRadius = 50.dp,
                onClick = { onShowSyncedLyricsChange(false) },
                text = "Static"
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = backgroundColor,
                contentColor = onBackgroundColor
            ),
            onClick = onMoreClick
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Lyrics options",
                tint = onBackgroundColor
            )
        }
    }
}
