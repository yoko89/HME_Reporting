package com.neklaway.hme_reporting.common.data.repository_impl

import com.neklaway.hme_reporting.common.data.dao.CarMileageDao
import com.neklaway.hme_reporting.common.data.entity.CarMileageEntity
import com.neklaway.hme_reporting.common.domain.repository.CarMileageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CarMileageRepositoryImpl @Inject constructor(
    val dao: CarMileageDao
) : CarMileageRepository {

    override suspend fun insert(carMileage: CarMileageEntity): Long {
        return dao.insert(carMileage)
    }

    override suspend fun insert(carMileageList: List<CarMileageEntity>): List<Long> {
        return dao.insert(carMileageList)
    }

    override suspend fun delete(carMileage: CarMileageEntity): Int {
        return dao.delete(carMileage)
    }

    override suspend fun update(carMileage: CarMileageEntity): Int {
        return dao.update(carMileage)
    }

    override suspend fun getById(id: Long): CarMileageEntity {
        return dao.getById(id)
    }

    override suspend fun getAllFlow(): Flow<List<CarMileageEntity>> {
        return dao.getAllFlow()
    }
}