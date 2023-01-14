package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.timesheet_route

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetTimeSheetRouteUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(timeSheetRoute:String){
        repository.setTimeSheetRoute(timeSheetRoute)
    }
}