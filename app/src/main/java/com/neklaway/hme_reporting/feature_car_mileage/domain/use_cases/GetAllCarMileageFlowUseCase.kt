package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.neklaway.hme_reporting.common.data.entity.toCarMileage
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCarMileageFlowUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(): Flow<Resource<List<CarMileage>>> = flow {
        emit(Resource.Loading())
        try {
            emitAll(repo.getAllFlow().map { carMileageEntities ->
                Resource.Success(carMileageEntities.map { carMileageEntity ->
                    carMileageEntity.toCarMileage()
                })
            })
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error: Can't get CarMileage"))
        }

    }
}