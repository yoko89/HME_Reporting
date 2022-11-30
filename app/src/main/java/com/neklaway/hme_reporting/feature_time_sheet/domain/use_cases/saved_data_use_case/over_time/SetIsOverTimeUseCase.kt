package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.over_time

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetIsOverTimeUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(isOverTime:Boolean){
        repository.setOverTimeDay(isOverTime)
    }
}