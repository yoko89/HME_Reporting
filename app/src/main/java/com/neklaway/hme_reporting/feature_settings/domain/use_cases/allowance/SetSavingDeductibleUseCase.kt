package com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetSavingDeductibleUseCase  @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(deductible:Int) {
        settingsRepository.setSavingDeductible(deductible)
    }
}
