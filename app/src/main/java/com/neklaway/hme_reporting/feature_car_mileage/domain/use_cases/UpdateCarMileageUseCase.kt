package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.toCarMileageEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

private const val TAG = "UpdateCarMileageUseCase"

class UpdateCarMileageUseCase @Inject constructor(
    val repo: CarMileageRepository
) {

    operator fun invoke(
        startMileage: Long?,
        startDate: Calendar?,
        startTime: Calendar?,
        endDate: Calendar?,
        endTime: Calendar?,
        endMileage: Long?,
        id: Long?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (startMileage == null) {
            emit(Resource.Error("Start Mileage can't be empty"))
            return@flow
        }
        if (startMileage < 0){
            emit(Resource.Error("Start Mileage can't be negative"))
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
        if (endMileage < startMileage) {
            emit(Resource.Error("End Mileage can't be less than Start Mileage"))
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
        if (id == null) {
            emit(Resource.Error("Car Mileage can't be updated"))
            return@flow
        }
        if (startDate.after(endDate)){
            emit(Resource.Error("Start Date Must be Before End Date"))
            return@flow
        }
        if (startTime.after(endTime)){
            emit(Resource.Error("Start time Must be Before End time"))
            return@flow
        }
        try {
            val carMileage =
                CarMileage(startDate, startTime, startMileage, endDate, endTime, endMileage, id)
            val result = repo.update(carMileage.toCarMileageEntity())
            if (result > 0) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't Update Car Mileage"))
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Update Car Mileage"))
            Log.d(TAG, "invoke: error ${e.message}")
        }
    }
}