package com.neklaway.hme_reporting.domain.use_cases.settings_use_case.ibau_id

import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetBreakDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(breakDuration:Float) : Flow<Resource<Boolean>> = flow{
        emit(Resource.Loading())

        try {
            if (breakDuration <0) {
                emit(Resource.Error("Value for break duration can't be negative"))
                return@flow
            }
            settingsRepository.setBreakDuration(breakDuration)
            emit(Resource.Success(true))
        }catch (e:Exception){
            emit(Resource.Error("Incorrect Value for break duration"))
        }

    }
}