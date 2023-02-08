package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_date

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import java.util.Calendar
import javax.inject.Inject

class SetCarMileageStartDateUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(date:Calendar?){
        repository.setCarMileageStartDate(date?.timeInMillis)
    }
}