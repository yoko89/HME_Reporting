package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toTimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTimeSheetByHMECodeIdUseCase @Inject constructor(
    val repo: TimeSheetRepository
) {

    operator fun invoke(hmeId: Long): Flow<Resource<Flow<List<TimeSheet>>>> = flow {
        emit(Resource.Loading())
        val result = repo.getByHMECodeId(hmeId).map{ it.map { it.toTimeSheet()}}
        emit(Resource.Success(result))
    }

}