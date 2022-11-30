package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.break_duration

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSavedBreakDurationUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(): Float? {
        return repository.getBreakDuration().first()
    }
}