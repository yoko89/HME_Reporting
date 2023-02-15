package com.neklaway.hme_reporting.common.domain.use_cases.currency_exchange_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCurrencyExchange
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrencyExchangeByIdUseCase @Inject constructor(
    val repo: CurrencyExchangeRepository
) {

    operator fun invoke(id: Long): Flow<Resource<CurrencyExchange>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toCurrencyExchange()
        emit(Resource.Success(result))
    }

}