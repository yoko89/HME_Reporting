package com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBreakDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): String {
        return settingsRepository.getBreakDuration().first().toString()
    }

}