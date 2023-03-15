package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toExpansesEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "InsertExpanseListUseCase"
class InsertExpanseListUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(expanseList: List<Expanse>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "invoke: Loading")
        try {
            val results = repo.insert(expanseList.map { it.toExpansesEntity() })
            Log.d(TAG, "invoke: Result is $results")
            val failed = results.find { result ->
                result == 0L
            }
            if (failed == null) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't insert Expanses"))
                Log.d(TAG, "invoke: Error $failed")
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            Log.d(TAG, "invoke: Error + ${e.localizedMessage}")
            emit(Resource.Error(e.message ?: "Error: Can't Insert Expanses"))
        }
    }
}
