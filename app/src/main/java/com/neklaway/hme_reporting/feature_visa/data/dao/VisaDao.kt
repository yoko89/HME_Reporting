package com.neklaway.hme_reporting.feature_visa.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.feature_visa.data.entity.VisaEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface VisaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(visa: VisaEntity):Long

    @Delete
    suspend fun delete(visa: VisaEntity):Int

    @Update
    suspend fun update(visa: VisaEntity):Int

    @Query("SELECT * FROM visaTable WHERE id = :id")
    suspend fun getById(id:Long): VisaEntity

    @Query("SELECT * FROM visaTable")
    fun getAllFlow(): Flow<List<VisaEntity>>

    @Query("SELECT * FROM visaTable")
    fun getAll(): List<VisaEntity>
}