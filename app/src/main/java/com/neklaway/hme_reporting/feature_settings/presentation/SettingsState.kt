package com.neklaway.hme_reporting.feature_settings.presentation

import androidx.compose.ui.graphics.ImageBitmap
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Theme

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
    val theme: Theme = Theme.Auto,
    val darkTheme: DarkTheme = DarkTheme.Auto,
) {
}
