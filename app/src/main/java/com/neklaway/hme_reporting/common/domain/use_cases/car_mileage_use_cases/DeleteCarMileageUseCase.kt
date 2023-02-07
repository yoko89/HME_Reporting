package com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases

import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.common.domain.model.toCarMileageEntity
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteCarMileageUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(carMileage: CarMileage): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(carMileage.toCarMileageEntity())
        if (result > 0) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't delete Car Mileage"))
        }
    }

}