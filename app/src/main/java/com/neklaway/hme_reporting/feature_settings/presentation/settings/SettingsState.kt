package com.neklaway.hme_reporting.feature_settings.presentation.settings

import androidx.compose.ui.graphics.ImageBitmap

data class SettingsState(
    val isIbauUser: Boolean = false,
    val isAutoClear: Boolean = false,
    val userName: String = "",
    val breakDuration: String = "",
    val isLoading: Boolean = false,
    val showSignaturePad: Boolean = false,
    val signature: ImageBitmap? = null,
    val visaReminder:String = ""
)
