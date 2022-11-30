package com.neklaway.hme_reporting.feature_time_sheet.domain.repository

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun insert(customerEntity: CustomerEntity): Long

    suspend fun delete(customerEntity: CustomerEntity): Int

    suspend fun update(customerEntity: CustomerEntity): Int

    suspend fun getById(id: Long): CustomerEntity

    fun getAllFlow(): Flow<List<CustomerEntity>>

    suspend fun getAll(): List<CustomerEntity>
}