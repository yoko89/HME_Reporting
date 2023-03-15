package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCurrencyExchange
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.Resource
import javax.inject.Inject

class GetAllCurrencyExchangeUseCase @Inject constructor(
    val repo: CurrencyExchangeRepository
) {

    suspend operator fun invoke(): Resource<List<CurrencyExchange>> {
        return try {
            Resource.Success(repo.getAll().map {  currencyExchangeEntity ->
                    currencyExchangeEntity.toCurrencyExchange()
                })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error: Can't get Currency Exchange")
        }

    }
}

