package com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): String {
        return settingsRepository.getUserName().first()
    }

}