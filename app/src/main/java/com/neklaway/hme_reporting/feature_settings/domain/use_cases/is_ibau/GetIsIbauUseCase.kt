package com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIsIbauUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Boolean {
        return settingsRepository.isIbauUser().first()
    }
}