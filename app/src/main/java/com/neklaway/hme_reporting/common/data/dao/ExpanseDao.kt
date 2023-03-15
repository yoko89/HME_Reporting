package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.ExpanseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpanseDao {

    @Insert
    suspend fun insert(expanseEntity: ExpanseEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expanseEntities: List<ExpanseEntity>): List<Long>

    @Delete
    suspend fun delete(expanseEntity: ExpanseEntity): Int

    @Update
    suspend fun update(expanseEntity: ExpanseEntity): Int

    @Update
    suspend fun update(expanseEntities: List<ExpanseEntity>): Int

    @Query("SELECT * FROM expansesTable")
    fun getAllFlow(): Flow<List<ExpanseEntity>>
    @Query("SELECT * FROM expansesTable")
    fun getAll(): List<ExpanseEntity>

    @Query("SELECT * FROM expansesTable WHERE id = :id")
    fun getById(id: Long): ExpanseEntity

    @Query("SELECT * FROM expansesTable WHERE HMEId = :id")
    fun getByHMECodeId(id: Long): Flow<List<ExpanseEntity>>
}