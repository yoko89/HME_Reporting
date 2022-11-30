package com.neklaway.hme_reporting.feature_time_sheet.data.repository_impl

import com.neklaway.hme_reporting.feature_time_sheet.data.dao.HMECodeDao
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.HMECodeEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.HMECodeRepository
import javax.inject.Inject

class HMECodeRepositoryImpl @Inject constructor(
    val dao: HMECodeDao
) : HMECodeRepository {
    override suspend fun insert(hmeCodeEntity: HMECodeEntity): Long {
        return dao.insert(hmeCodeEntity)
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