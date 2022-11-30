package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.date

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import java.util.Calendar
import javax.inject.Inject

class SetDateUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(date:Calendar?){
        repository.setDate(date?.timeInMillis)
    }
}