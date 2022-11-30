package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.timesheet_route

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTimeSheetRouteUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(): String? {
        return repository.getTimeSheetRoute().first()
    }
}