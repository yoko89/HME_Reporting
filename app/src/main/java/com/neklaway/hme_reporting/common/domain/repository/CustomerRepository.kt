package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun insert(customerEntity: CustomerEntity): Long

    suspend fun insert(customerEntities: List<CustomerEntity>): List<Long>

    suspend fun delete(customerEntity: CustomerEntity): Int

    suspend fun update(customerEntity: CustomerEntity): Int

    suspend fun getById(id: Long): CustomerEntity

    fun getAllFlow(): Flow<List<CustomerEntity>>

    suspend fun getAll(): List<CustomerEntity>
}