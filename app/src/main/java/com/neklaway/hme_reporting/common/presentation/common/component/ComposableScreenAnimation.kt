package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ComposableScreenAnimation(content: @Composable AnimatedVisibilityScope.() -> Unit) {
    AnimatedVisibility(
        visibleState = remember {
            MutableTransitionState(
                initialState = false
            ).apply { targetState = true }
        },
        modifier = Modifier.fillMaxSize(),
        enter = slideInHorizontally().plus(fadeIn()),
        exit = slideOutHorizontally().plus(fadeOut()),
        content = content
    )
}