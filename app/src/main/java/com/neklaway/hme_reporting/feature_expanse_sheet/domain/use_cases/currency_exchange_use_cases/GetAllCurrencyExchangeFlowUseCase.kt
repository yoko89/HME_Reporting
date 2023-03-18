package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCurrencyExchange
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetAllCurrencyExchangeFlowUseCase @Inject constructor(
    val insertCurrencyExchangeUseCase: InsertCurrencyExchangeUseCase,
    val repo: CurrencyExchangeRepository
) {

    operator fun invoke(): Flow<Resource<List<CurrencyExchange>>> = flow {
        emit(Resource.Loading())
        try {
            val currencyFlow = repo.getAllFlow().map { currencyExchangeEntities ->
                if (currencyExchangeEntities.isEmpty()){
                    insertCurrencyExchangeUseCase("AED",1f).collect()
                }
                Resource.Success(currencyExchangeEntities.map { currencyExchangeEntity ->
                    currencyExchangeEntity.toCurrencyExchange()
                })
            }
            emitAll(currencyFlow)
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error: Can't get Currency Exchange"))
        }

    }
}

