package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toCurrencyExchangeEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertCurrencyExchangeListUseCase @Inject constructor(
    val repo: CurrencyExchangeRepository
) {

    operator fun invoke(currencyExchangeList: List<CurrencyExchange>): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())

            try {
                val results =
                    repo.insert(currencyExchangeList.map { it.toCurrencyExchangeEntity() })
                val failed = results.find { result ->
                    result == 0L
                }
                if (failed == null) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Error: Can't insert Currency Exchange"))
                }
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Error: Can't Insert Currency Exchange"))
            }
        }
}
