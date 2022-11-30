package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.ibau_id

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetIBAUIdUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(isIBAU: Long) {
        savedDataRepository.setIBAUId(isIBAU)
    }
}