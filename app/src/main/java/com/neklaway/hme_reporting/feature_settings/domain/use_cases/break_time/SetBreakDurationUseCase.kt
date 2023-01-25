package com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time

import android.util.Log
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


private const val TAG = "set_break_duration_use_case"
class SetBreakDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(breakDuration:Float) : Flow<Resource<Boolean>> = flow{
        emit(Resource.Loading())
        Log.d(TAG, "invoke: use case started")
        try {
            if (breakDuration <0) {
                emit(Resource.Error("Value for break duration can't be negative"))
                return@flow
            }
            Log.d(TAG, "invoke: break time set $breakDuration")
            settingsRepository.setBreakDuration(breakDuration)
            emit(Resource.Success(true))
        }catch (e:Exception){
            emit(Resource.Error("Incorrect Value for break duration"))
        }

    }
}