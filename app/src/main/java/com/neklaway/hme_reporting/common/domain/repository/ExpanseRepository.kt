package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.ExpanseEntity
import kotlinx.coroutines.flow.Flow

interface ExpanseRepository {


    suspend fun insert(expanseEntity: ExpanseEntity): Long

    suspend fun insert(expanseEntities: List<ExpanseEntity>): List<Long>

    suspend fun delete(expanseEntity: ExpanseEntity): Int

    suspend fun update(expanseEntity: ExpanseEntity): Int

    suspend fun update(expanseEntities: List<ExpanseEntity>): Int

    fun getAllFlow(): Flow<List<ExpanseEntity>>
    suspend fun getAll(): List<ExpanseEntity>
    fun getById(id: Long): ExpanseEntity

    fun getByHMECodeId(id: Long): Flow<List<ExpanseEntity>>

}

