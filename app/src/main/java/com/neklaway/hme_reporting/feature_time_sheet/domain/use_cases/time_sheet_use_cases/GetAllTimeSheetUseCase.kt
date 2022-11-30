package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toTimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.TimeSheetRepository
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
