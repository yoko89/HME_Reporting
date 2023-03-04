package com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class Set8HAllowanceUseCase  @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(allowance:Int) {
        settingsRepository.set8HDayAllowance(allowance)
    }
}
