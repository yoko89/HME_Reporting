package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_mileage

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetCarMileageEndMileageUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(mileage: Long?) {
        repository.setCarMileageEndMileage(mileage)
    }
}