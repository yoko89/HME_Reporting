package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.expanse_sheet_route

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetExpanseSheetRouteUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(): String? {
        return repository.getExpanseSheetRoute().first()
    }
}