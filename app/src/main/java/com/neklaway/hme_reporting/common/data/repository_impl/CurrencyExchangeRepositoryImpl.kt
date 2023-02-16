package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.CurrencyExchangeDao
import com.neklaway.hme_reporting.common.data.entity.CurrencyExchangeEntity
import com.neklaway.hme_reporting.common.domain.repository.CurrencyExchangeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyExchangeRepositoryImpl @Inject constructor(
    val dao:CurrencyExchangeDao
) : CurrencyExchangeRepository{
    override suspend fun insert(currencyExchangeEntity: CurrencyExchangeEntity): Long {
        return dao.insert(currencyExchangeEntity)
    }

    override suspend fun insert(currencyExchangeEntities: List<CurrencyExchangeEntity>): List<Long> {
        return dao.insert(currencyExchangeEntities)
    }

    override suspend fun delete(currencyExchangeEntity: CurrencyExchangeEntity): Int {
        return dao.delete(currencyExchangeEntity)
    }

    override suspend fun update(currencyExchangeEntity: CurrencyExchangeEntity): Int {
        return dao.update(currencyExchangeEntity)
    }

    override fun getAll(): Flow<List<CurrencyExchangeEntity>> {
        return dao.getAll()
    }

    override suspend fun getById(id: Long): CurrencyExchangeEntity {
        return dao.getById(id)
    }
}