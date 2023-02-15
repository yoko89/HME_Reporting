package com.neklaway.hme_reporting.common.domain.use_cases.currency_exchange_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCurrencyExchange
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCurrencyExchangeFlowUseCase @Inject constructor(
    val repo: CurrencyExchangeRepository
) {

    operator fun invoke(): Flow<Resource<List<CurrencyExchange>>> = flow {
        emit(Resource.Loading())
        try {
            emitAll(repo.getAll().map { currencyExchangeEntities ->
                Resource.Success(currencyExchangeEntities.map { currencyExchangeEntity ->
                    currencyExchangeEntity.toCurrencyExchange()
                })
            })
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error: Can't get Currency Exchange"))
        }

    }
}

