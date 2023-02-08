package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_Mileage

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetCarMileageStartMileageUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(mileage: Long?) {
        repository.setCarMileageStartMileage(mileage)
    }
}