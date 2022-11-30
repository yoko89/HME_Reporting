package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.travel_distance

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetTravelDistanceUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(travelDistance:Int?){
        repository.setTraveledDistance(travelDistance)
    }
}