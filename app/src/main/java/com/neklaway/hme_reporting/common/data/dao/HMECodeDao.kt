package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.HMECodeEntity

@Dao
interface HMECodeDao {

    @Insert
    suspend fun insert(hmeCode: HMECodeEntity):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(hmeCodes: List<HMECodeEntity>):List<Long>

    @Delete
    suspend fun delete(hmeCode: HMECodeEntity):Int

    @Update
    suspend fun update(hmeCode: HMECodeEntity):Int

    @Query("SELECT * FROM hmeCodeTable ORDER BY code ASC")
    suspend fun getAll(): List<HMECodeEntity>

    @Query("SELECT * FROM hmeCodeTable WHERE id = :id")
    suspend fun getById(id: Long): HMECodeEntity

    @Query("SELECT * FROM hmeCodeTable WHERE customerId = :customerId ORDER BY code ASC")
    suspend fun getByCustomerId(customerId: Long): List<HMECodeEntity>

}