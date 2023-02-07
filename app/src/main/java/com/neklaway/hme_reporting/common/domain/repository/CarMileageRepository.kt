package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.CarMileageEntity
import kotlinx.coroutines.flow.Flow

interface CarMileageRepository {

    suspend fun insert(carMileage: CarMileageEntity): Long

    suspend fun insert(carMileageList: List<CarMileageEntity>): List<Long>

    suspend fun delete(carMileage: CarMileageEntity): Int

    suspend fun update(carMileage: CarMileageEntity): Int

    suspend fun getById(id: Long): CarMileageEntity

    suspend fun getAllFlow(): Flow<List<CarMileageEntity>>

}