package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.work_end

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import java.util.Calendar
import javax.inject.Inject

class SetWorkEndUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(workEnd:Calendar?){
        repository.setWorkEnd(workEnd?.timeInMillis)
    }
}