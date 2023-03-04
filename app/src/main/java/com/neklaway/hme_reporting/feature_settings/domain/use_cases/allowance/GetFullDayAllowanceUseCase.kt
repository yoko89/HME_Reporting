package com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetFullDayAllowanceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Int {
        return settingsRepository.getFullDayAllowance().first()
    }
}