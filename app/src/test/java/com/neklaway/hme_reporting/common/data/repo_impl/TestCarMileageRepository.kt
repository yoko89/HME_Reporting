package com.neklaway.hme_reporting.common.data.repo_impl

import com.neklaway.hme_reporting.common.data.entity.CarMileageEntity
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestCarMileageRepository : CarMileageRepository {
    private val carMileageListData: MutableList<CarMileageEntity> = mutableListOf()

    override suspend fun insert(carMileage: CarMileageEntity): Long {
        return if (carMileageListData.add(carMileage)
        ) 1L else -1
    }

    override suspend fun insert(carMileageList: List<CarMileageEntity>): List<Long> {
        return if (carMileageListData.addAll(carMileageList)) listOf(1L, 2L) else emptyList()
    }

    override suspend fun delete(carMileage: CarMileageEntity): Int {
        return if (carMileageListData.remove(carMileage)) 1 else -1
    }

    override suspend fun update(carMileage: CarMileageEntity): Int {
        return if (carMileageListData.remove(carMileageListData.find { it.id == carMileage.id })
            and carMileageListData.add(carMileage)
        ) 1 else -1
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