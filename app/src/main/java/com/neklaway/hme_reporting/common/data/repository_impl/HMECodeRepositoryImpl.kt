package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.HMECodeDao
import com.neklaway.hme_reporting.common.data.entity.HMECodeEntity
import com.neklaway.hme_reporting.common.domain.repository.HMECodeRepository
import javax.inject.Inject

class HMECodeRepositoryImpl @Inject constructor(
    val dao: HMECodeDao
) : HMECodeRepository {

    override suspend fun insert(hmeCodeEntity: HMECodeEntity): Long {
        return dao.insert(hmeCodeEntity)
    }

    override suspend fun insert(hmeCodeEntities: List<HMECodeEntity>): List<Long> {
        return dao.insert(hmeCodeEntities)
    }

    override suspend fun delete(hmeCodeEntity: HMECodeEntity): Int {
        return dao.delete(hmeCodeEntity)
    }

    override suspend fun update(hmeCodeEntity: HMECodeEntity): Int {
        return dao.update(hmeCodeEntity)
    }

    override suspend fun getAll(): List<HMECodeEntity> {
        return dao.getAll()
    }

    override suspend fun getById(id: Long): HMECodeEntity {
        return dao.getById(id)
    }

    override suspend fun getByCustomerId(customerId: Long): List<HMECodeEntity> {
        return dao.getByCustomerId(customerId)
    }
}