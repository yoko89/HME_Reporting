package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.VisaEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface VisaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(visa: VisaEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(visas: List<VisaEntity>): List<Long>

    @Delete
    suspend fun delete(visa: VisaEntity): Int

    @Update
    suspend fun update(visa: VisaEntity): Int

    @Query("SELECT * FROM visaTable WHERE id = :id")
    suspend fun getById(id: Long): VisaEntity

    @Query("SELECT * FROM visaTable ORDER BY date ASC")
    fun getAllFlow(): Flow<List<VisaEntity>>

    @Query("SELECT * FROM visaTable ORDER BY date ASC")
    fun getAll(): List<VisaEntity>
}