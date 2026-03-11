package com.rve.musicplayer.presentation.components.scoped

import com.rve.musicplayer.presentation.navigation.navigateSafely

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.rve.musicplayer.presentation.navigation.Screen
import com.rve.musicplayer.presentation.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlayerArtistNavigationEffect(
    navController: NavHostController,
    sheetCollapsedTargetY: Float,
    sheetMotionController: SheetMotionController,
    playerViewModel: PlayerViewModel
) {
    LaunchedEffect(navController, sheetCollapsedTargetY) {
        playerViewModel.artistNavigationRequests.collectLatest { artistId ->
            sheetMotionController.snapCollapsed(sheetCollapsedTargetY)
            playerViewModel.collapsePlayerSheet()

            navController.navigateSafely(Screen.ArtistDetail.createRoute(artistId)) {
                launchSingleTop = true
            }
        }
    }
}
