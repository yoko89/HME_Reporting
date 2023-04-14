package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toCurrencyExchangeEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "UpdateVisaUseCase"

class UpdateCurrencyExchangeUseCase @Inject constructor(
    val repo: CurrencyExchangeRepository
) {

    operator fun invoke(
        currencyName: String,
        rate: Float?,
        id: Long?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (currencyName.trim().isBlank()) {
            emit(Resource.Error("Currency name can't be blank"))
            return@flow
        }
        if (rate == null) {
            emit(Resource.Error("Exchange rate can't be Null"))
            return@flow
        }
        if (rate == 0f) {
            emit(Resource.Error("Exchange rate can't be zero"))
            return@flow
        }
        if (rate < 0f) {
            emit(Resource.Error("Exchange rate can't be negative"))
            return@flow
        }
        try {
            val currencyExchange = CurrencyExchange(currencyName, rate,id)
            val result = repo.update(currencyExchange.toCurrencyExchangeEntity())
            if (result > 0) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't Update Currency Exchange"))
                Log.d(TAG, "invoke: error $result")
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Update Currency Exchange"))
            Log.d(TAG, "invoke: error ${e.message}")
        }
    }
}