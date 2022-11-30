package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.is_weekend

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetIsWeekendUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(isWeekend: Boolean) {
        savedDataRepository.setWeekEnd(isWeekend)
    }
}