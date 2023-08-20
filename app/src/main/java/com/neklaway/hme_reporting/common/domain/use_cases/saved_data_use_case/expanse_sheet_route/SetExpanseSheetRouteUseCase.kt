package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.expanse_sheet_route

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetExpanseSheetRouteUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(timeSheetRoute:String){
        repository.setExpanseSheetRoute(timeSheetRoute)
    }
}