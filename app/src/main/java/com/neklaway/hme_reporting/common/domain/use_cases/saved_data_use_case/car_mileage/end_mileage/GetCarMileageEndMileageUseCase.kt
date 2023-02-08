package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_mileage

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCarMileageEndMileageUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(): Long? {
        return repository.getCarMileageEndDate().first()
    }
}