package com.neklaway.hme_reporting.common.domain.repository

import com.neklaway.hme_reporting.common.data.entity.IBAUCodeEntity

interface IBAUCodeRepository {

    suspend fun insert(ibauCodeEntity: IBAUCodeEntity): Long

    suspend fun insert(ibauCodeEntities: List<IBAUCodeEntity>): List<Long>

    suspend fun delete(ibauCodeEntity: IBAUCodeEntity): Int

    suspend fun update(ibauCodeEntity: IBAUCodeEntity): Int

    suspend fun getAll(): List<IBAUCodeEntity>

    suspend fun getById(id: Long): IBAUCodeEntity

    fun getByHMECodeID(hmeId: Long): List<IBAUCodeEntity>
}