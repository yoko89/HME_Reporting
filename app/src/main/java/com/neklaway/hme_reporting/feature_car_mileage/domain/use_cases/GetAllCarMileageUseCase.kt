package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.neklaway.hme_reporting.common.data.entity.toCarMileage
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.utils.Resource
import javax.inject.Inject

class GetAllCarMileageUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(): Resource<List<CarMileage>> {
        return try {
            Resource.Success(repo.getAll().map { carMileageEntity ->
                carMileageEntity.toCarMileage()
            })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error: Can't get CarMileage")
        }

    }
}