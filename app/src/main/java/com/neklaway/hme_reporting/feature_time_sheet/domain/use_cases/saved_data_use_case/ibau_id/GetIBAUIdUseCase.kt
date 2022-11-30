package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.ibau_id

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIBAUIdUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(): Long {
        return savedDataRepository.getIBAUId().first()
    }

}