package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_end

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import java.util.Calendar
import javax.inject.Inject

class SetTravelEndUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(travelEnd:Calendar?){
        repository.setTravelEnd(travelEnd?.timeInMillis)
    }
}