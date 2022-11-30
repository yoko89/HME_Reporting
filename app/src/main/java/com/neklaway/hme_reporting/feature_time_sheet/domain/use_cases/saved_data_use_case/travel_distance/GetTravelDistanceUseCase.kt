package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.travel_distance

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTravelDistanceUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(): Int? {
        return repository.getTraveledDistance().first()
    }
}