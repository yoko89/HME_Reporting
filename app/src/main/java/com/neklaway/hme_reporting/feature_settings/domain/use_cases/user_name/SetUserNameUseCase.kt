package com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetUserNameUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(userName:String) {
        settingsRepository.setUserName(userName)
    }
}