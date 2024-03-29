package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_start

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import java.util.Calendar
import javax.inject.Inject

class SetTravelStartUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(travelStart:Calendar?){
        repository.setTravelStart(travelStart?.timeInMillis)
    }
}