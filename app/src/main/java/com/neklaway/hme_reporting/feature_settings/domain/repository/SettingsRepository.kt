package com.neklaway.hme_reporting.feature_settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun setIbauUser(isIbauUser: Boolean)

    fun isIbauUser(): Flow<Boolean>

    suspend fun setAutoClear(autoClear: Boolean)

    fun isAutoClear(): Flow<Boolean>

    suspend fun setBreakDuration(breakTime: Float)

    fun getBreakDuration(): Flow<Float>

    suspend fun setUserName(userName: String)

    fun getUserName(): Flow<String>

    suspend fun setVisaReminder(reminder: Int)

    fun getVisaReminder(): Flow<Int>
    suspend fun set8HDayAllowance(allowance: Int)
    suspend fun setFullDayAllowance(allowance: Int)
    fun get8HDayAllowance(): Flow<Int>
    fun getFullDayAllowance(): Flow<Int>
    suspend fun setSavingDeductible(deductible:Int)
    fun getSavingDeductible():Flow<Int>
}