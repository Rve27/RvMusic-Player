package com.rve.musicplayer.presentation.components.scoped

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.rve.musicplayer.presentation.navigation.Screen
import com.rve.musicplayer.presentation.navigation.navigateSafely
import com.rve.musicplayer.presentation.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlayerAlbumNavigationEffect(
    navController: NavHostController,
    sheetCollapsedTargetY: Float,
    sheetMotionController: SheetMotionController,
    playerViewModel: PlayerViewModel
) {
    LaunchedEffect(navController, sheetCollapsedTargetY) {
        playerViewModel.albumNavigationRequests.collectLatest { albumId ->
            sheetMotionController.snapCollapsed(sheetCollapsedTargetY)
            playerViewModel.collapsePlayerSheet()

            navController.navigateSafely(Screen.AlbumDetail.createRoute(albumId)) {
                launchSingleTop = false
                navController.currentBackStackEntry?.destination?.route?.let { currentRoute ->
                    if (currentRoute == Screen.AlbumDetail.route) {
                        popUpTo(Screen.AlbumDetail.route) { inclusive = true }
                    }
                }
            }
        }
    }
}
