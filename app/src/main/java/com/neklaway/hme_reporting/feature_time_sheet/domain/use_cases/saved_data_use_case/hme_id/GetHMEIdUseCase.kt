package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.hme_id

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetHMEIdUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(): Long {
        return savedDataRepository.getHMEId().first()
    }

}