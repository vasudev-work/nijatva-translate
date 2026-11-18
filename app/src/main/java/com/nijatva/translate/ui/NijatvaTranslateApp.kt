package com.nijatva.translate.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun NijatvaTranslateApp(
    viewModel: TranslateViewModel,
    sharedText: String? = null,
) {
    var showSettings by rememberSaveable { mutableStateOf(false) }

    val incoming = remember(sharedText) { sharedText?.trim().orEmpty() }
    LaunchedEffect(incoming) {
        if (incoming.isNotEmpty()) viewModel.onInputChange(incoming)
    }

    if (showSettings) {
        BackHandler { showSettings = false }
        SettingsScreen(viewModel = viewModel, onBack = { showSettings = false })
    } else {
        TranslateScreen(viewModel = viewModel, onOpenSettings = { showSettings = true })
    }
}
