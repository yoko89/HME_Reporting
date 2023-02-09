package com.neklaway.hme_reporting.feature_visa.domain.use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.feature_visa.domain.model.toVisaEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertVisaListUseCase @Inject constructor(
    val repo: VisaRepository
) {

    operator fun invoke(visas: List<Visa>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val results = repo.insert(visas.map { it.toVisaEntity() })
            val failed = results.find { result ->
                result == 0L
            }
            if (failed == null) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't insert Visas"))
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Insert Visa"))
        }
    }
}
