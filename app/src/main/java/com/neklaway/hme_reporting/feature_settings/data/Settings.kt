package com.neklaway.hme_reporting.feature_settings.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class Settings @Inject constructor(@ApplicationContext context: Context) {

    private val settingsDataStore = context.dataStore

    companion object {
        private val user_name = stringPreferencesKey("user_name")
        private val ibau_user = booleanPreferencesKey("ibau_user")
        private val break_duration = floatPreferencesKey("break_duration")
        private val auto_clear = booleanPreferencesKey("auto_clear")
        private val visa_reminder = intPreferencesKey("visa_reminder")
    }

    suspend fun setIbauUser(isIbauUser: Boolean) {
        settingsDataStore.edit { settings ->
            settings[ibau_user] = isIbauUser
        }
    }

    val isIbauUser: Flow<Boolean> = settingsDataStore.data.map { settings ->
        settings[ibau_user] ?: false
    }

    suspend fun setAutoClear(autoClear: Boolean) {
        settingsDataStore.edit { settings ->
            settings[auto_clear] = autoClear
        }
    }

    val isAutoClear: Flow<Boolean> = settingsDataStore.data.map { settings ->
        settings[auto_clear] ?: false
    }

    suspend fun setBreakDuration(breakTime: Float) {
        settingsDataStore.edit { settings ->
            settings[break_duration] = breakTime
        }
    }

    val getBreakDuration: Flow<Float> = settingsDataStore.data.map { settings ->
        settings[break_duration] ?: 0f
    }

    suspend fun setUserName(userName: String) {
        settingsDataStore.edit { settings ->
            settings[user_name] = userName
        }
    }

    val getUserName: Flow<String> = settingsDataStore.data.map { settings ->
        settings[user_name] ?: ""
    }

    suspend fun setVisaReminder(reminder: Int) {
        settingsDataStore.edit { settings ->
            settings[visa_reminder] = reminder
        }
    }

    val getVisaReminder: Flow<Int> = settingsDataStore.data.map { settings ->
        settings[visa_reminder] ?: 30
    }

}