package com.neklaway.hme_reporting.feature_settings.data.repository_impl

import com.neklaway.hme_reporting.feature_settings.data.Settings
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settings: Settings
) : SettingsRepository {

    override suspend fun setIbauUser(isIbauUser: Boolean) {
        settings.setIbauUser(isIbauUser)
    }

    override fun isIbauUser(): Flow<Boolean> {
        return settings.isIbauUser
    }

    override suspend fun setAutoClear(autoClear: Boolean) {
        settings.setAutoClear(autoClear)
    }

    override fun isAutoClear(): Flow<Boolean> {
        return settings.isAutoClear
    }

    override suspend fun setUserName(userName: String) {
        settings.setUserName(userName)
    }

    override fun getUserName(): Flow<String> {
        return settings.getUserName
    }

    override suspend fun setBreakDuration(breakTime: Float) {
        settings.setBreakDuration(breakTime)
    }

    override fun getBreakDuration(): Flow<Float> {
        return settings.getBreakDuration
    }

    override suspend fun setVisaReminder(reminder: Int) {
        settings.setVisaReminder(reminder)
    }

    override fun getVisaReminder(): Flow<Int> {
       return settings.getVisaReminder
    }


}