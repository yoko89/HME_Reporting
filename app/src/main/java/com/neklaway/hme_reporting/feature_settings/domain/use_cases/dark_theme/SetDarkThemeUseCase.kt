package com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.DarkTheme
import javax.inject.Inject

class SetDarkThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(theme: DarkTheme) {
        settingsRepository.setDarkTheme(theme)
    }
}