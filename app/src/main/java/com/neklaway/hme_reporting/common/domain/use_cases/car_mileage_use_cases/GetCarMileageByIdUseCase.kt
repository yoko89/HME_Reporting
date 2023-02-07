package com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCarMileage
import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCarMileageByIdUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(id: Long): Flow<Resource<CarMileage>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toCarMileage()
        emit(Resource.Success(result))
    }

}