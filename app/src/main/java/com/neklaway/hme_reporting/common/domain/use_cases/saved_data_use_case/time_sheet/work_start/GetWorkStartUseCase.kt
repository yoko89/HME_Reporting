package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.work_start

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class GetWorkStartUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke():Calendar?{
        val timeInMills = repository.getWorkStart().first()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dubai"))
        return if (timeInMills != null){
            cal.timeInMillis = timeInMills
            cal
        }else{
            null
        }



    }
}