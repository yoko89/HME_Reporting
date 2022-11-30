package com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetIsAutoClearUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(autoClear: Boolean) {
        settingsRepository.setAutoClear(autoClear)
    }
}