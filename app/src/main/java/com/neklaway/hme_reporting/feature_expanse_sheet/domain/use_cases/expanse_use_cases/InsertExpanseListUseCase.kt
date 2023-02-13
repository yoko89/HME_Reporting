package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toExpansesEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertExpanseListUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(expanseList: List<Expanse>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val results = repo.insert(expanseList.map { it.toExpansesEntity() })
            val failed = results.find { result ->
                result == 0L
            }
            if (failed == null) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't insert Expanses"))
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Insert Expanses"))
        }
    }
}
