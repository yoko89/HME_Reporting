package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.is_weekend

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIsWeekendUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(): Boolean {
        return savedDataRepository.isWeekend().first()
    }
}