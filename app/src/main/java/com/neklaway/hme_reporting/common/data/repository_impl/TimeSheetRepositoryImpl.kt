package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.TimeSheetDao
import com.neklaway.hme_reporting.common.data.entity.TimeSheetEntity
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeSheetRepositoryImpl @Inject constructor(
    val dao: TimeSheetDao
) : TimeSheetRepository {

    override suspend fun insert(timeSheetEntity: TimeSheetEntity): Long {
        return dao.insert(timeSheetEntity)
    }

    override suspend fun insert(timeSheetEntities: List<TimeSheetEntity>): List<Long> {
        return dao.insert(timeSheetEntities)
    }

    override suspend fun delete(timeSheetEntity: TimeSheetEntity): Int {
        return dao.delete(timeSheetEntity)
    }

    override suspend fun update(timeSheetEntity: TimeSheetEntity): Int {
        return dao.update(timeSheetEntity)
    }

    override suspend fun update(timeSheetEntities: List<TimeSheetEntity>): Int {
        return dao.update(timeSheetEntities)
    }

    override suspend fun getById(id: Long): TimeSheetEntity {
        return dao.getById(id)
    }

    override suspend fun getByHMECodeId(hmeId: Long): Flow<List<TimeSheetEntity>> {
        return dao.getByHMECodeId(hmeId)
    }

    override suspend fun getByIBAUCodeId(ibauId: Long): Flow<List<TimeSheetEntity>> {
        return dao.getByIBAUCodeId(ibauId)
    }

    override suspend fun getAll(): List<TimeSheetEntity> {
        return dao.getAll()
    }
}