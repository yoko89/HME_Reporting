package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.VisaDao
import com.neklaway.hme_reporting.common.data.entity.VisaEntity
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VisaRepositoryImpl @Inject constructor(
    val dao: VisaDao
) : VisaRepository {

    override suspend fun insert(visa: VisaEntity): Long {
        return dao.insert(visa)
    }

    override suspend fun insert(visas: List<VisaEntity>): List<Long> {
        return dao.insert(visas)
    }

    override suspend fun delete(visa: VisaEntity): Int {
        return dao.delete(visa)
    }

    override suspend fun update(visa: VisaEntity): Int {
        return dao.update(visa)
    }

    override suspend fun getById(id: Long): VisaEntity {
        return dao.getById(id)
    }

    override suspend fun getAllFlow(): Flow<List<VisaEntity>> {
        return dao.getAllFlow()
    }

    override suspend fun getAll(): List<VisaEntity> {
        return dao.getAll()
    }

}