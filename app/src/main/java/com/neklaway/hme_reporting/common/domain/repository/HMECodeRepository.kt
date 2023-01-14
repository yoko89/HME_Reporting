package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.HMECodeEntity

interface HMECodeRepository {

    suspend fun insert(hmeCodeEntity: HMECodeEntity): Long

    suspend fun insert(hmeCodeEntities: List<HMECodeEntity>): List<Long>

    suspend fun delete(hmeCodeEntity: HMECodeEntity): Int

    suspend fun update(hmeCodeEntity: HMECodeEntity): Int

    suspend fun getAll(): List<HMECodeEntity>

    suspend fun getById(id: Long): HMECodeEntity

    suspend fun getByCustomerId(customerId: Long): List<HMECodeEntity>
}