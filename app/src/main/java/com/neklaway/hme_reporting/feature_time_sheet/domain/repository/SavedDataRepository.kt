package com.neklaway.hme_reporting.feature_time_sheet.domain.repository

import androidx.datastore.preferences.core.edit
import com.neklaway.hme_reporting.feature_time_sheet.data.SavedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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


}