package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.main_route

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMainRouteUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    operator fun invoke(): Flow<String> {
        return repository.getMainRoute()
    }
}