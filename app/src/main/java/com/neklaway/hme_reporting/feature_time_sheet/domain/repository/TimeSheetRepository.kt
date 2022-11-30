package com.neklaway.hme_reporting.feature_time_sheet.domain.repository

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.TimeSheetEntity
import kotlinx.coroutines.flow.Flow

interface TimeSheetRepository {

    suspend fun insert(timeSheetEntity: TimeSheetEntity): Long

    suspend fun delete(timeSheetEntity: TimeSheetEntity): Int

    suspend fun update(timeSheetEntity: TimeSheetEntity): Int

    suspend fun update(timeSheetEntities: List<TimeSheetEntity>): Int

    suspend fun getById(id: Long): TimeSheetEntity

    suspend fun getByHMECodeId(hmeId: Long): Flow<List<TimeSheetEntity>>

    suspend fun getByIBAUCodeId(ibauId: Long): Flow<List<TimeSheetEntity>>

    suspend fun getAll():List<TimeSheetEntity>

}