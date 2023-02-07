package com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.common.domain.model.toCarMileageEntity
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class InsertCarMileageUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(
        startMileage: Long?,
        startDate: Calendar?,
        startTime: Calendar?,
        endDate: Calendar?,
        endTime: Calendar?,
        endMileage: Long?,
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (startMileage == null) {
            emit(Resource.Error("Start Mileage can't be empty"))
            return@flow
        }
        if (startDate == null) {
            emit(Resource.Error("Start Date can't be blank"))
            return@flow
        }
        if (startTime == null) {
            emit(Resource.Error("Start Time can't be blank"))
            return@flow
        }
        if (endMileage == null) {
            emit(Resource.Error("End Mileage can't be empty"))
            return@flow
        }
        if (endDate == null) {
            emit(Resource.Error("End Date can't be blank"))
            return@flow
        }
        if (endTime == null) {
            emit(Resource.Error("End Time can't be blank"))
            return@flow
        }
        try {
            val carMileage =
                CarMileage(startDate, startTime, startMileage, endDate, endTime, endMileage)
            val result = repo.insert(carMileage.toCarMileageEntity())
            if (result > 0) {
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