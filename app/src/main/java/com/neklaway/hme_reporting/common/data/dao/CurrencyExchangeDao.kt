package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.CurrencyExchangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyExchangeDao {

    @Insert
    suspend fun insert(currencyExchangeEntity: CurrencyExchangeEntity): Long

    @Insert
    suspend fun insert(currencyExchangeEntities: List<CurrencyExchangeEntity>): List<Long>

    @Delete
    suspend fun delete(currencyExchangeEntity: CurrencyExchangeEntity): Int

    @Update
    suspend fun update(currencyExchangeEntity: CurrencyExchangeEntity): Int

    @Query("SELECT * FROM currencyExchangeTable")
    suspend fun getAll(): Flow<List<CurrencyExchangeEntity>>

    @Query("SELECT * FROM currencyExchangeTable WHERE id = :id")
    fun getById(id: Long): CurrencyExchangeEntity

}