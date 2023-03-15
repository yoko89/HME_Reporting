package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.CarMileageEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CarMileageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carMileage: CarMileageEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(carMileageList: List<CarMileageEntity>): List<Long>

    @Delete
    suspend fun delete(carMileage: CarMileageEntity): Int

    @Update
    suspend fun update(carMileage: CarMileageEntity): Int

    @Query("SELECT * FROM carMileageTable WHERE id = :id")
    suspend fun getById(id: Long): CarMileageEntity

    @Query("SELECT * FROM carMileageTable")
    fun getAllFlow(): Flow<List<CarMileageEntity>>

    @Query("SELECT * FROM carMileageTable")
    fun getAll(): List<CarMileageEntity>

}