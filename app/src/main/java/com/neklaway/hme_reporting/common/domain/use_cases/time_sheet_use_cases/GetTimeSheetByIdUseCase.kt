package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.common.data.entity.toTimeSheet
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTimeSheetByIdUseCase @Inject constructor(
    val repo: TimeSheetRepository
) {

    operator fun invoke(id: Long): Flow<Resource<TimeSheet>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toTimeSheet()
        emit(Resource.Success(result))
    }

}