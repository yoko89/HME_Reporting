package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.is_travel_day

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetIsSavedTravelDayUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(isTravelDay: Boolean) {
        savedDataRepository.setTravelDay(isTravelDay)
    }
}