package com.neklaway.hme_reporting.common.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("saved_data")

class SavedData @Inject constructor(@ApplicationContext context: Context) {

    private val savedDataDataStore = context.dataStore

    companion object {
        val customer_id = longPreferencesKey("customer_id")
        val hme_id = longPreferencesKey("hme_id")
        val ibau_id = longPreferencesKey("ibau_id")
        val date = longPreferencesKey("date")
        val travel_start = longPreferencesKey("travel_start")
        val work_start = longPreferencesKey("work_start")
        val work_end = longPreferencesKey("work_end")
        val travel_end = longPreferencesKey("travel_end")
        val break_duration = floatPreferencesKey("break_duration")
        val traveled_distance = intPreferencesKey("travel_distance")
        val over_time_day = booleanPreferencesKey("over_time")
        val weekend = booleanPreferencesKey("weekend")
        val travel_day = booleanPreferencesKey("travel_day")
        val timesheet_route = stringPreferencesKey("timesheet_route")
        val car_mileage_start_date = longPreferencesKey("car_mileage_start_date")
        val car_mileage_start_time = longPreferencesKey("car_mileage_start_time")
        val car_mileage_start_mileage = longPreferencesKey("car_mileage_start_mileage")
        val car_mileage_end_date = longPreferencesKey("car_mileage_end_date")
        val car_mileage_end_time = longPreferencesKey("car_mileage_end_time")
        val car_mileage_end_mileage = longPreferencesKey("car_mileage_end_mileage")
    }

    suspend fun setCustomerId(customerId: Long) {
        savedDataDataStore.edit { settings ->
            settings[customer_id] = customerId
        }
    }

    val getCustomerId: Flow<Long> = savedDataDataStore.data.map { settings ->
        settings[customer_id] ?: -1
    }

    suspend fun setHMEId(HMEId: Long) {
        savedDataDataStore.edit { settings ->
            settings[hme_id] = HMEId
        }
    }

    val getHMEId: Flow<Long> = savedDataDataStore.data.map { settings ->
        settings[hme_id] ?: -1
    }

    suspend fun setIBAUId(IBAUId: Long) {
        savedDataDataStore.edit { settings ->
            settings[ibau_id] = IBAUId
        }
    }

    val getIBAUId: Flow<Long> = savedDataDataStore.data.map { settings ->
        settings[ibau_id] ?: -1
    }

    suspend fun setWeekEnd(isWeekend: Boolean) {
        savedDataDataStore.edit { settings ->
            settings[weekend] = isWeekend
        }
    }

    val isWeekend: Flow<Boolean> = savedDataDataStore.data.map { settings ->
        settings[weekend] ?: false
    }

    suspend fun setTravelDay(isTravelDay: Boolean) {
        savedDataDataStore.edit { settings ->
            settings[travel_day] = isTravelDay
        }
    }

    val isTravelDay: Flow<Boolean> = savedDataDataStore.data.map { settings ->
        settings[travel_day] ?: false
    }

    suspend fun setWorkStart(workStartInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[work_start] = workStartInMills ?: -1L
        }
    }

    val getWorkStart: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[work_start].let {
            when (it) {
                -1L -> null
                else -> it
            }

        }
    }

    suspend fun setTravelStart(travelStartInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[travel_start] = travelStartInMills ?: -1L
        }
    }

    val getTravelStart: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[travel_start].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }


    suspend fun setWorkEnd(workEndInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[work_end] = workEndInMills ?: -1L
        }
    }

    val getWorkEnd: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[work_end].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setTravelEnd(travelEndInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[travel_end] = travelEndInMills ?: -1L
        }
    }

    val getTravelEnd: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[travel_end].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setDate(dateInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[date] = dateInMills ?: -1L
        }
    }

    val getDate: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[date].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setBreakDuration(breakTime: Float?) {
        savedDataDataStore.edit { settings ->
            settings[break_duration] = breakTime ?: -1F
        }
    }

    val getBreakDuration: Flow<Float?> = savedDataDataStore.data.map { settings ->
        settings[break_duration].let {
            when (it) {
                -1F -> null
                else -> it
            }
        }
    }

    suspend fun setTraveledDistance(distance: Int?) {
        savedDataDataStore.edit { settings ->
            settings[traveled_distance] = distance ?: -1
        }
    }

    val getTraveledDistance: Flow<Int?> = savedDataDataStore.data.map { settings ->
        settings[traveled_distance].let {
            when (it) {
                -1 -> null
                else -> it
            }
        }
    }


    suspend fun setOverTimeDay(isOverTime: Boolean) {
        savedDataDataStore.edit { settings ->
            settings[over_time_day] = isOverTime
        }
    }

    val isOverTimeDay: Flow<Boolean> = savedDataDataStore.data.map { settings ->
        settings[over_time_day] ?: false
    }

    suspend fun setTimeSheetRoute(route: String) {
        savedDataDataStore.edit { settings ->
            settings[timesheet_route] = route
        }
    }

    val getTimeSheetRoute: Flow<String?> = savedDataDataStore.data.map { settings ->
        settings[timesheet_route]
    }

    suspend fun setCarMileageStartDate(carMileageStartDateInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[car_mileage_start_date] = carMileageStartDateInMills ?: -1L
        }
    }

    val getCarMileageStartDate: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[car_mileage_start_date].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setCarMileageStartTime(carMileageStartTimeInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[car_mileage_start_time] = carMileageStartTimeInMills ?: -1L
        }
    }

    val getCarMileageStartTime: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[car_mileage_start_time].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setCarMileageStartMileage(carMileageStartMileage: Long?) {
        savedDataDataStore.edit { settings ->
            settings[car_mileage_start_mileage] = carMileageStartMileage ?: -1L
        }
    }

    val getCarMileageStartMileage: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[car_mileage_start_mileage].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setCarMileageEndDate(carMileageEndDateInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[car_mileage_end_date] = carMileageEndDateInMills ?: -1L
        }
    }

    val getCarMileageEndDate: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[car_mileage_end_date].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setCarMileageEndTime(carMileageEndTimeInMills: Long?) {
        savedDataDataStore.edit { settings ->
            settings[car_mileage_end_time] = carMileageEndTimeInMills ?: -1L
        }
    }

    val getCarMileageEndTime: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[car_mileage_end_time].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

    suspend fun setCarMileageEndMileage(carMileageEndMileage: Long?) {
        savedDataDataStore.edit { settings ->
            settings[car_mileage_end_mileage] = carMileageEndMileage ?: -1L
        }
    }

    val getCarMileageEndMileage: Flow<Long?> = savedDataDataStore.data.map { settings ->
        settings[car_mileage_end_mileage].let {
            when (it) {
                -1L -> null
                else -> it
            }
        }
    }

}


