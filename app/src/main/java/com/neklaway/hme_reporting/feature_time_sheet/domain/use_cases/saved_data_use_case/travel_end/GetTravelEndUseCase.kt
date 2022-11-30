package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.travel_end

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class GetTravelEndUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke():Calendar?{
        val timeInMills = repository.getTravelEnd().first()

        val cal = Calendar.getInstance()
        return if (timeInMills != null){
            cal.timeInMillis = timeInMills
            cal
        }else{
            null
        }



    }
}