package com.neklaway.hme_reporting.feature_time_sheet.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.IBAUCodeEntity

@Dao
interface IBAUCodeDao {

    @Insert
    suspend fun insert(ibauCode: IBAUCodeEntity):Long

    @Delete
    suspend fun delete(ibauCode: IBAUCodeEntity):Int

    @Update
    suspend fun update(ibauCode: IBAUCodeEntity):Int

    @Query("SELECT * FROM ibauCodeTable")
    suspend fun getAll(): List<IBAUCodeEntity>

    @Query("SELECT * FROM ibauCodeTable WHERE id = :id")
    fun getById(id: Long): IBAUCodeEntity

    @Query("SELECT * FROM ibauCodeTable WHERE HMEId = :hmeId")
    fun getByHMECodeId(hmeId: Long): List<IBAUCodeEntity>
}