package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.CustomerDao
import com.neklaway.hme_reporting.common.data.entity.CustomerEntity
import com.neklaway.hme_reporting.common.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CustomerRepositoryImpl @Inject constructor(
    val dao: CustomerDao
) : CustomerRepository {

    override suspend fun insert(customerEntity: CustomerEntity): Long {
        return dao.insert(customerEntity)
    }

    override suspend fun insert(customerEntities: List<CustomerEntity>): List<Long> {
        return dao.insert(customerEntities)
    }

    override suspend fun delete(customerEntity: CustomerEntity): Int {
        return dao.delete(customerEntity)
    }

    override suspend fun update(customerEntity: CustomerEntity): Int {
        return dao.update(customerEntity)
    }

    override suspend fun getById(id: Long): CustomerEntity {
        return dao.getById(id)
    }

    override fun getAllFlow(): Flow<List<CustomerEntity>> {
        return dao.getAllFlow()
    }

    override suspend fun getAll(): List<CustomerEntity> {
        return dao.getAll()
    }
}