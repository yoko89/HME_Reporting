package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.TimeSheetEntity
import kotlinx.coroutines.flow.Flow

interface TimeSheetRepository {

    suspend fun insert(timeSheetEntity: TimeSheetEntity): Long

    suspend fun insert(timeSheetEntities: List<TimeSheetEntity>): List<Long>

    suspend fun delete(timeSheetEntity: TimeSheetEntity): Int

    suspend fun update(timeSheetEntity: TimeSheetEntity): Int

    suspend fun update(timeSheetEntities: List<TimeSheetEntity>): Int

    suspend fun getById(id: Long): TimeSheetEntity

    suspend fun getByHMECodeId(hmeId: Long): Flow<List<TimeSheetEntity>>

    suspend fun getByIBAUCodeId(ibauId: Long): Flow<List<TimeSheetEntity>>

    suspend fun getAll():List<TimeSheetEntity>

}