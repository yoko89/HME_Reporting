package com.neklaway.hme_reporting.feature_settings.presentation

import androidx.compose.ui.graphics.ImageBitmap
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.Theme

data class SettingsState(
    val isIbauUser: Boolean = false,
    val isAutoClear: Boolean = false,
    val userName: String = "",
    val breakDuration: ResourceWithString<Float> = ResourceWithString.Success(0f,""),
    val isLoading: Boolean = false,
    val showSignaturePad: Boolean = false,
    val signature: ImageBitmap? = null,
    val visaReminder: ResourceWithString<Int> = ResourceWithString.Success(0,""),
    val fullDayAllowance :ResourceWithString<Int> = ResourceWithString.Success(0,""),
    val _8HAllowance: ResourceWithString<Int> = ResourceWithString.Success(0,""),
    val savingDeductible:ResourceWithString<Int> = ResourceWithString.Success(0,""),
    val theme: Theme = Theme.Auto,
    val darkTheme: DarkTheme = DarkTheme.Auto,
)
