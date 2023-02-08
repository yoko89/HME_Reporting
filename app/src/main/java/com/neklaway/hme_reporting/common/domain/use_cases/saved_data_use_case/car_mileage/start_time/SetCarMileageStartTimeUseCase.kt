package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_time

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import java.util.*
import javax.inject.Inject

class SetCarMileageStartTimeUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(time: Calendar?) {
        repository.setCarMileageStartTime(time?.timeInMillis)
    }
}