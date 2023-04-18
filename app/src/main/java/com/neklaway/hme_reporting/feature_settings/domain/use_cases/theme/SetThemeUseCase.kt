package com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Theme
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(theme: Theme) {
        settingsRepository.setTheme(theme)
    }
}