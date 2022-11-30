package com.neklaway.hme_reporting.feature_time_sheet.data.repository_impl

import com.neklaway.hme_reporting.feature_time_sheet.data.dao.IBAUCodeDao
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.IBAUCodeEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.IBAUCodeRepository
import javax.inject.Inject


class IBAUCodeRepositoryImpl @Inject constructor(
    val dao: IBAUCodeDao
) : IBAUCodeRepository {
    override suspend fun insert(ibauCodeEntity: IBAUCodeEntity): Long {
        return dao.insert(ibauCodeEntity)
    }

    override suspend fun delete(ibauCodeEntity: IBAUCodeEntity): Int {
        return dao.delete(ibauCodeEntity)
    }

    override suspend fun update(ibauCodeEntity: IBAUCodeEntity): Int {
        return dao.update(ibauCodeEntity)
    }

    override suspend fun getAll(): List<IBAUCodeEntity> {
        return dao.getAll()
    }

    override suspend fun getById(id: Long): IBAUCodeEntity {
        return dao.getById(id)
    }

    override fun getByHMECodeID(hmeId: Long): List<IBAUCodeEntity> {
        return dao.getByHMECodeId(hmeId)
    }

}