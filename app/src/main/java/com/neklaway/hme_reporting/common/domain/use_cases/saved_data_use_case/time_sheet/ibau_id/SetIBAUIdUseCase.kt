package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetIBAUIdUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(isIBAU: Long) {
        savedDataRepository.setIBAUId(isIBAU)
    }
}