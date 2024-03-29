package com.neklaway.hme_reporting.common.domain.repository

import kotlinx.coroutines.flow.Flow

interface SavedDataRepository {

    suspend fun setCustomerId(customerId: Long)

    fun getCustomerId(): Flow<Long>

    suspend fun setHMEId(hmeId: Long)

    fun getHMEId(): Flow<Long>

    suspend fun setIBAUId(ibauId: Long)

    fun getIBAUId(): Flow<Long>

    suspend fun setWeekEnd(isWeekend: Boolean)

    fun isWeekend(): Flow<Boolean>

    suspend fun setTravelDay(isTravelDay: Boolean)

    fun isTravelDay(): Flow<Boolean>

    suspend fun setWorkStart(workStartInMills: Long?)

    fun getWorkStart(): Flow<Long?>

    suspend fun setTravelStart(travelStartInMills: Long?)

    fun getTravelStart(): Flow<Long?>

    suspend fun setWorkEnd(workEndInMills: Long?)

    fun getWorkEnd(): Flow<Long?>

    suspend fun setTravelEnd(travelEndInMills: Long?)

    fun getTravelEnd(): Flow<Long?>

    suspend fun setDate(dateInMills: Long?)

    fun getDate(): Flow<Long?>

    suspend fun setBreakDuration(breakTime: Float?)

    fun getBreakDuration(): Flow<Float?>

    suspend fun setTraveledDistance(distance: Int?)

    fun getTraveledDistance(): Flow<Int?>

    suspend fun setOverTimeDay(isOverTime: Boolean)

    fun isOverTimeDay(): Flow<Boolean>

    suspend fun setTimeSheetRoute(route: String)

    fun getTimeSheetRoute(): Flow<String?>
    suspend fun setExpanseSheetRoute(route: String)

    fun getExpanseSheetRoute(): Flow<String?>
    suspend fun setMainRoute(route: String)

    fun getMainRoute(): Flow<String>

    suspend fun setCarMileageStartDate(carMileageStartDateInMills: Long?)

    fun getCarMileageStartDate(): Flow<Long?>

    suspend fun setCarMileageStartTime(carMileageStartTimeInMills: Long?)

    fun getCarMileageStartTime(): Flow<Long?>

    suspend fun setCarMileageStartMileage(carMileageStartMileage: Long?)

    fun getCarMileageStartMileage(): Flow<Long?>

    suspend fun setCarMileageEndDate(carMileageEndDateInMills: Long?)

    fun getCarMileageEndDate(): Flow<Long?>

    suspend fun setCarMileageEndTime(carMileageEndTimeInMills: Long?)

    fun getCarMileageEndTime(): Flow<Long?>

    suspend fun setCarMileageEndMileage(carMileageEndMileage: Long?)

    fun getCarMileageEndMileage(): Flow<Long?>

}