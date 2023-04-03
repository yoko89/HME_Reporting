package com.neklaway.hme_reporting.feature_settings.presentation

import androidx.compose.ui.graphics.ImageBitmap

data class SettingsState(
    val isIbauUser: Boolean = false,
    val isAutoClear: Boolean = false,
    val userName: String = "",
    val breakDuration: String = "",
    val isLoading: Boolean = false,
    val showSignaturePad: Boolean = false,
    val signature: ImageBitmap? = null,
    val visaReminder: String = "",
    val fullDayAllowance :String ="",
    val _8HAllowance: String = "",
    val savingDeductible:String = "",
) {
}
