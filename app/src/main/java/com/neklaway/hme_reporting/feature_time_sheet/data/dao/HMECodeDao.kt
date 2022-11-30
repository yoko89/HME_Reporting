package com.neklaway.hme_reporting.feature_time_sheet.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.HMECodeEntity

@Dao
interface HMECodeDao {

    @Insert
    suspend fun insert(hmeCode: HMECodeEntity):Long

    @Delete
    suspend fun delete(hmeCode: HMECodeEntity):Int

    @Update
    suspend fun update(hmeCode: HMECodeEntity):Int

    @Query("SELECT * FROM hmeCodeTable")
    suspend fun getAll(): List<HMECodeEntity>

    @Query("SELECT * FROM hmeCodeTable WHERE id = :id")
    fun getById(id: Long): HMECodeEntity

    @Query("SELECT * FROM hmeCodeTable WHERE customerId = :customerId")
    fun getByCustomerId(customerId: Long): List<HMECodeEntity>

}