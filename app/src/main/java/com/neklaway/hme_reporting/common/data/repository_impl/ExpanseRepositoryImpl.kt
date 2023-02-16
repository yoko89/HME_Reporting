package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.ExpanseDao
import com.neklaway.hme_reporting.common.data.entity.ExpanseEntity
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpanseRepositoryImpl @Inject constructor(
    val dao: ExpanseDao
) : ExpanseRepository {
    override suspend fun insert(expanseEntity: ExpanseEntity): Long {
        return dao.insert(expanseEntity)
    }

    override suspend fun insert(expanseEntities: List<ExpanseEntity>): List<Long> {
        return dao.insert(expanseEntities)
    }

    override suspend fun delete(expanseEntity: ExpanseEntity): Int {
        return dao.delete(expanseEntity)
    }

    override suspend fun update(expanseEntity: ExpanseEntity): Int {
        return dao.update(expanseEntity)
    }

    override suspend fun update(expanseEntities: List<ExpanseEntity>): Int {
        return dao.update(expanseEntities)
    }

    override fun getAll(): Flow<List<ExpanseEntity>> {
        return dao.getAll()
    }

    override fun getById(id: Long): Flow<ExpanseEntity> {
        return dao.getById(id)
    }

    override fun getByHMECodeId(id: Long): Flow<List<ExpanseEntity>> {
        return dao.getByHMECodeId(id)
    }
}