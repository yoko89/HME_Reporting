package com.neklaway.hme_reporting.feature_time_sheet.domain.repository

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.IBAUCodeEntity

interface IBAUCodeRepository {

    suspend fun insert(ibauCodeEntity: IBAUCodeEntity): Long

    suspend fun delete(ibauCodeEntity: IBAUCodeEntity): Int

    suspend fun update(ibauCodeEntity: IBAUCodeEntity): Int

    suspend fun getAll(): List<IBAUCodeEntity>

    suspend fun getById(id: Long): IBAUCodeEntity

    fun getByHMECodeID(hmeId: Long): List<IBAUCodeEntity>
}