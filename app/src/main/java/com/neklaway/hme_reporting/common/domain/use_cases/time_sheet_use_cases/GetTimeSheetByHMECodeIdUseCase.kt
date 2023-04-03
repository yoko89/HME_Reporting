package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.common.data.entity.toTimeSheet
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetTimeSheetByHMECodeIdUseCase @Inject constructor(
    val repo: TimeSheetRepository
) {

    operator fun invoke(hmeId: Long): Flow<Resource<List<TimeSheet>>> = flow {
        emit(Resource.Loading())
        emitAll(repo.getByHMECodeId(hmeId).map {
            Resource.Success(it.map { it.toTimeSheet() })
        })
    }
}