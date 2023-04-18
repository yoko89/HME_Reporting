package com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Theme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<Theme> {
        return settingsRepository.getTheme()
    }

}