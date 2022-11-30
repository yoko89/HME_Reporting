package com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetVisaReminderUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): Int {
        return settingsRepository.getVisaReminder().first()
    }

}