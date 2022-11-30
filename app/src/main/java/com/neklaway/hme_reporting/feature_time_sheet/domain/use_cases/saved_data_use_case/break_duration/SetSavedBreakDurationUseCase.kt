package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.break_duration

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetSavedBreakDurationUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(breakDuration:Float?){
        repository.setBreakDuration(breakDuration)
    }
}