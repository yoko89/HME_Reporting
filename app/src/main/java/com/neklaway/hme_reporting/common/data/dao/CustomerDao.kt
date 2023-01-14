package com.neklaway.hme_reporting.common.data.dao

import androidx.room.*
import com.neklaway.hme_reporting.common.data.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customer: CustomerEntity):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customers: List<CustomerEntity>):List<Long>

    @Delete
    suspend fun delete(customer: CustomerEntity):Int

    @Update
    suspend fun update(customer: CustomerEntity):Int

    @Query("SELECT * FROM customerTable WHERE id = :id")
    suspend fun getById(id:Long): CustomerEntity

    @Query("SELECT * FROM customerTable")
    fun getAllFlow(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customerTable")
    fun getAll(): List<CustomerEntity>
}