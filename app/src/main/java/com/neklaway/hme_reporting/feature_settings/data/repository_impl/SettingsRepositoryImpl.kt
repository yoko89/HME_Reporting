package com.neklaway.hme_reporting.feature_settings.data.repository_impl

import android.util.Log
import com.neklaway.hme_reporting.feature_settings.data.Settings
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Theme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "settingsRepo"

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
        Log.d(TAG, "setBreakDuration: $breakTime")
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

    override suspend fun set8HDayAllowance(allowance: Int) {
        settings.set8HDayAllowance(allowance)
    }

    override suspend fun setFullDayAllowance(allowance: Int) {
        settings.setFullDayAllowance(allowance)
    }

    override fun get8HDayAllowance(): Flow<Int> {
        return settings.get8HDayAllowance
    }

    override fun getFullDayAllowance(): Flow<Int> {
        return settings.getFullDayAllowance
    }

    override suspend fun setSavingDeductible(deductible: Int) {
        settings.setSavingDeductible(deductible)
    }

    override fun getSavingDeductible(): Flow<Int> {
        return settings.getSavingDeductible
    }

    override suspend fun setTheme(theme: Theme) {
        settings.setTheme(theme)
    }

    override fun getTheme(): Flow<Theme> {
        return settings.getTheme
    }

    override suspend fun setDarkTheme(theme: DarkTheme) {
        settings.setDarkTheme(theme)
    }

    override fun getDarkTheme(): Flow<DarkTheme> {
        return settings.getDarkTheme
    }
}