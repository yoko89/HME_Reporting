package com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetIsIbauUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(isIbau: Boolean) {
        settingsRepository.setIbauUser(isIbau)
    }
}