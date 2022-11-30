package com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIsAutoClearUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Boolean {
        return settingsRepository.isAutoClear().first()
    }
}