package com.neklaway.hme_reporting.feature_visa.domain.repository

import com.neklaway.hme_reporting.feature_visa.data.entity.VisaEntity
import kotlinx.coroutines.flow.Flow

interface VisaRepository {

    suspend fun insert(visa:VisaEntity): Long

    suspend fun delete(visa:VisaEntity): Int

    suspend fun update(visa:VisaEntity): Int

    suspend fun getById(id: Long): VisaEntity

    suspend fun getAllFlow(): Flow<List<VisaEntity>>

    suspend fun  getAll(): List<VisaEntity>

}