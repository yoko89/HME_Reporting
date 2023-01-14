package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.model.toTimeSheetEntity
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTimeSheetUseCase @Inject constructor(
    val repo: TimeSheetRepository
) {

    operator fun invoke(timeSheet: TimeSheet): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val result = repo.delete(timeSheet.toTimeSheetEntity())
        if (result > 0) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't delete Time Sheet"))
        }
    }

}