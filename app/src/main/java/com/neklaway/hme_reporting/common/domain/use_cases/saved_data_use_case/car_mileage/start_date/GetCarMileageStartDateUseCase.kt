package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_date

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class GetCarMileageStartDateUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke():Calendar?{
        val timeInMills = repository.getCarMileageStartDate().first()

        val cal = Calendar.getInstance()
        return if (timeInMills != null){
            cal.timeInMillis = timeInMills
            cal
        } else {
            null
        }
    }
}