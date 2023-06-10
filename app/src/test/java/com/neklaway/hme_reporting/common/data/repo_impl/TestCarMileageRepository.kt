package com.neklaway.hme_reporting.common.data.repo_impl

import com.neklaway.hme_reporting.common.data.entity.CarMileageEntity
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestCarMileageRepository : CarMileageRepository {
    private val carMileageListData: MutableList<CarMileageEntity> = mutableListOf()

    override suspend fun insert(carMileage: CarMileageEntity): Long {
        carMileageListData.add(carMileage)
        return 1L
    }

    override suspend fun insert(carMileageList: List<CarMileageEntity>): List<Long> {
        carMileageListData.addAll(carMileageList)
        return listOf(1L, 2L)
    }

    override suspend fun delete(carMileage: CarMileageEntity): Int {
        carMileageListData.remove(carMileage)
        return 1
    }

    override suspend fun update(carMileage: CarMileageEntity): Int {
        carMileageListData.remove(carMileageListData.find { it.id == carMileage.id })
        carMileageListData.add(carMileage)
        return 1
    }

    override suspend fun getById(id: Long): CarMileageEntity {
        return carMileageListData.find { it.id == id } ?: CarMileageEntity(0, 0, 0, 0, 0, 0, 0)
    }

    override suspend fun getAllFlow(): Flow<List<CarMileageEntity>> = flow {
        emit(carMileageListData)
    }

    override fun getAll(): List<CarMileageEntity> {
        return carMileageListData
    }
}