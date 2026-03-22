package com.rve.musicplayer.presentation.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset

// Push: Enter from Right
fun enterTransition(
    animationSpec: FiniteAnimationSpec<IntOffset>
) = slideInHorizontally(
    animationSpec = animationSpec,
    initialOffsetX = { it }
)

// Push: Exit to Left with Fade
fun exitTransition(
    slideOutAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeOutAnimationSpec: FiniteAnimationSpec<Float>
) = slideOutHorizontally(
    animationSpec = slideOutAnimationSpec,
    targetOffsetX = { -it / 3 }
) + fadeOut(
    animationSpec = fadeOutAnimationSpec
)

// Pop: Enter from Left (Parallax, No Fade)
fun popEnterTransition(
    slideInAnimationSpec: FiniteAnimationSpec<IntOffset>,
    scaleInAnimationSpec: FiniteAnimationSpec<Float>
) = slideInHorizontally(
    animationSpec = slideInAnimationSpec,
    initialOffsetX = { -it / 3 } // Start from Left (parallax)
) + scaleIn(
    animationSpec = scaleInAnimationSpec,
    initialScale = 0.9f // Slight zoom in for depth
)

// Pop: Exit to Right with Scale Down (No Fade)
fun popExitTransition(
    slideOutAnimationSpec: FiniteAnimationSpec<IntOffset>,
    scaleOutAnimationSpec: FiniteAnimationSpec<Float>
) = slideOutHorizontally(
    animationSpec = slideOutAnimationSpec,
    targetOffsetX = { it }
) + scaleOut(
    animationSpec = scaleOutAnimationSpec,
    targetScale = 0.75f,
    transformOrigin = TransformOrigin(0.5f, 0.5f)
)
