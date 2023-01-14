package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.is_travel_day

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIsSavedTravelDayUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(): Boolean {
        return savedDataRepository.isTravelDay().first()
    }
}