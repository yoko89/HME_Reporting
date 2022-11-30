package com.neklaway.hme_reporting.feature_settings.domain.repository

import androidx.datastore.preferences.core.edit
import com.neklaway.hme_reporting.feature_settings.data.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {

    suspend fun setIbauUser(isIbauUser: Boolean)

    fun isIbauUser(): Flow<Boolean>

    suspend fun setAutoClear(autoClear: Boolean)

    fun isAutoClear(): Flow<Boolean>

    suspend fun setBreakDuration(breakTime: Float)

    fun getBreakDuration(): Flow<Float>

    suspend fun setUserName(userName:String)

    fun getUserName(): Flow<String>

    suspend fun setVisaReminder(reminder: Int)

    fun getVisaReminder(): Flow<Int>
}