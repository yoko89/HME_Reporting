package com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.over_time

import com.neklaway.hme_reporting.common.domain.repository.SavedDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIsOverTimeUseCase @Inject constructor(
    val repository: SavedDataRepository
){
    suspend operator fun invoke():Boolean{
        return repository.isOverTimeDay().first()
    }
}