package com.neklaway.hme_reporting.common.domain.use_cases.currency_exchange_use_cases

import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toCurrencyExchangeEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteCurrencyExchangeUseCase @Inject constructor(
    val repo: CurrencyExchangeRepository
) {

    operator fun invoke(currencyExchange: CurrencyExchange): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(currencyExchange.toCurrencyExchangeEntity())
        if (result > 0) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't delete Currency Exchange"))
        }
    }

}