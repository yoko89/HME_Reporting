package com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.common.domain.model.toCarMileageEntity
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertCarMileageListUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(carMileageList: List<CarMileage>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val results = repo.insert(carMileageList.map { it.toCarMileageEntity() })
            val failed = results.find { result ->
                result == 0L
            }
            if (failed == null) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't insert Car Mileage"))
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Insert Car Mileage"))
        }
    }
}
