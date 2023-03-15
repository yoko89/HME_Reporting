package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.CurrencyExchangeEntity
import kotlinx.coroutines.flow.Flow

interface CurrencyExchangeRepository {

    suspend fun insert(currencyExchangeEntity: CurrencyExchangeEntity): Long

    suspend fun insert(currencyExchangeEntities: List<CurrencyExchangeEntity>): List<Long>

    suspend fun delete(currencyExchangeEntity: CurrencyExchangeEntity): Int

    suspend fun update(currencyExchangeEntity: CurrencyExchangeEntity): Int

    fun getAllFlow(): Flow<List<CurrencyExchangeEntity>>
    suspend fun getAll(): List<CurrencyExchangeEntity>

    suspend fun getById(id: Long): CurrencyExchangeEntity
}