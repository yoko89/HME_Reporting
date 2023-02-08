package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_Mileage

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCarMileageStartMileageUseCase @Inject constructor(
    val repository: SavedDataRepository
) {
    suspend operator fun invoke(): Long? {
        return repository.getCarMileageStartDate().first()
    }
}