package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.common.data.entity.toTimeSheet
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.utils.Resource
import java.io.IOException
import javax.inject.Inject

class GetAllTimeSheetUseCase @Inject constructor(
    val repo: TimeSheetRepository
) {

    suspend operator fun invoke(): Resource<List<TimeSheet>> {
        return try {
            Resource.Success(repo.getAll().map { timeSheetEntity ->
                timeSheetEntity.toTimeSheet()
            })
        } catch (e: IOException) {
            Resource.Error(e.message ?: "Can't get TimeSheet List")
        }
    }
}
