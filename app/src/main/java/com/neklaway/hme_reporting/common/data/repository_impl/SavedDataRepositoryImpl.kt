package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.SavedData
import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedDataRepositoryImpl @Inject constructor(
    private val savedData: SavedData
) : SavedDataRepository {

    override suspend fun setCustomerId(customerId: Long) {
        savedData.setCustomerId(customerId)
    }

    override fun getCustomerId(): Flow<Long> {
        return savedData.getCustomerId
    }

    override suspend fun setHMEId(hmeId: Long) {
        savedData.setHMEId(hmeId)
    }

    override fun getHMEId(): Flow<Long> {
        return savedData.getHMEId
    }

    override suspend fun setIBAUId(ibauId: Long) {
        savedData.setIBAUId(ibauId)
    }

    override fun getIBAUId(): Flow<Long> {
        return savedData.getIBAUId
    }

    override suspend fun setWeekEnd(isWeekend: Boolean) {
        savedData.setWeekEnd(isWeekend)
    }

    override fun isWeekend(): Flow<Boolean> {
        return savedData.isWeekend
    }

    override suspend fun setTravelDay(isTravelDay: Boolean) {
        savedData.setTravelDay(isTravelDay)
    }

    override fun isTravelDay(): Flow<Boolean> {
        return savedData.isTravelDay
    }

    override suspend fun setWorkStart(workStartInMills: Long?) {
        savedData.setWorkStart(workStartInMills)
    }

    override fun getWorkStart(): Flow<Long?> {
        return savedData.getWorkStart
    }

    override suspend fun setTravelStart(travelStartInMills: Long?) {
        savedData.setTravelStart(travelStartInMills)
    }

    override fun getTravelStart(): Flow<Long?> {
        return savedData.getTravelStart
    }

    override suspend fun setWorkEnd(workEndInMills: Long?) {
        savedData.setWorkEnd(workEndInMills)
    }

    override fun getWorkEnd(): Flow<Long?> {
        return savedData.getWorkEnd
    }

    override suspend fun setTravelEnd(travelEndInMills: Long?) {
        savedData.setTravelEnd(travelEndInMills)
    }

    override fun getTravelEnd(): Flow<Long?> {
        return savedData.getTravelEnd
    }

    override suspend fun setDate(dateInMills: Long?) {
        savedData.setDate(dateInMills)
    }

    override fun getDate(): Flow<Long?> {
        return savedData.getDate
    }

    override suspend fun setBreakDuration(breakTime: Float?) {
        savedData.setBreakDuration(breakTime)
    }

    override fun getBreakDuration(): Flow<Float?> {
        return savedData.getBreakDuration
    }

    override suspend fun setTraveledDistance(distance: Int?) {
        savedData.setTraveledDistance(distance)
    }

    override fun getTraveledDistance(): Flow<Int?> {
        return savedData.getTraveledDistance
    }

    override suspend fun setOverTimeDay(isOverTime: Boolean) {
        savedData.setOverTimeDay(isOverTime)
    }

    override fun isOverTimeDay(): Flow<Boolean> {
        return savedData.isOverTimeDay
    }

    override suspend fun setTimeSheetRoute(route: String) {
        savedData.setTimeSheetRoute(route)
    }

    override fun getTimeSheetRoute(): Flow<String?> {
        return savedData.getTimeSheetRoute
    }
    override suspend fun setExpanseSheetRoute(route: String) {
        savedData.setExpanseSheetRoute(route)
    }

    override fun getExpanseSheetRoute(): Flow<String?> {
        return savedData.getExpanseSheetRoute
    }
    override suspend fun setMainRoute(route: String) {
        savedData.setMainRoute(route)
    }

    override fun getMainRoute(): Flow<String> {
        return savedData.getMainRoute
    }

    override suspend fun setCarMileageStartDate(carMileageStartDateInMills: Long?) {
        savedData.setCarMileageStartDate(carMileageStartDateInMills)
    }

    override fun getCarMileageStartDate(): Flow<Long?> {
        return savedData.getCarMileageStartDate
    }

    override suspend fun setCarMileageStartTime(carMileageStartTimeInMills: Long?) {
        savedData.setCarMileageStartTime(carMileageStartTimeInMills)
    }

    override fun getCarMileageStartTime(): Flow<Long?> {
        return savedData.getCarMileageStartTime
    }
    override suspend fun setCarMileageStartMileage(carMileageStartMileage: Long?) {
        savedData.setCarMileageStartMileage(carMileageStartMileage)
    }

    override fun getCarMileageStartMileage(): Flow<Long?> {
        return savedData.getCarMileageStartMileage
    }
    override suspend fun setCarMileageEndDate(carMileageEndDateInMills: Long?) {
        savedData.setCarMileageEndDate(carMileageEndDateInMills)
    }

    override fun getCarMileageEndDate(): Flow<Long?> {
        return savedData.getCarMileageEndDate
    }

    override suspend fun setCarMileageEndTime(carMileageEndTimeInMills: Long?) {
        savedData.setCarMileageEndTime(carMileageEndTimeInMills)
    }

    override fun getCarMileageEndTime(): Flow<Long?> {
        return savedData.getCarMileageEndTime
    }
    override suspend fun setCarMileageEndMileage(carMileageEndMileage: Long?) {
        savedData.setCarMileageEndMileage(carMileageEndMileage)
    }

    override fun getCarMileageEndMileage(): Flow<Long?> {
        return savedData.getCarMileageEndMileage
    }

}