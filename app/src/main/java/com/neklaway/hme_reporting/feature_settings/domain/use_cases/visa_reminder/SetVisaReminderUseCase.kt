package com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetVisaReminderUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(reminder:Int) {
        settingsRepository.setVisaReminder(reminder)
    }
}