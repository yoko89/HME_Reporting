package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.TimeSheetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeSheetDao {

    @Insert
    suspend fun insert(timeSheet: TimeSheetEntity):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(timeSheets: List<TimeSheetEntity>):List<Long>

    @Delete
    suspend fun delete(timeSheet: TimeSheetEntity):Int

    @Update
    suspend fun update(timeSheet: TimeSheetEntity):Int

    @Update
    suspend fun update(timeSheets: List<TimeSheetEntity>):Int

    @Query("SELECT * FROM timeSheetTable")
    suspend fun getAll(): List<TimeSheetEntity>

    @Query("SELECT * FROM timeSheetTable WHERE id = :id")
    fun getById(id: Long): TimeSheetEntity

    @Query("SELECT * FROM timeSheetTable WHERE HMEId = :id")
    fun getByHMECodeId(id: Long): Flow<List<TimeSheetEntity>>

    @Query("SELECT * FROM timeSheetTable WHERE IBAUId = :id")
    fun getByIBAUCodeId(id: Long): Flow<List<TimeSheetEntity>>

}