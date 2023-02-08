package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_distance

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetTravelDistanceUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(travelDistance:Int?){
        repository.setTraveledDistance(travelDistance)
    }
}