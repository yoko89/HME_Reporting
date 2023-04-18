package com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.DarkTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDarkThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<DarkTheme> {
        return settingsRepository.getDarkTheme()
    }

}