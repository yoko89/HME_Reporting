package com.neklaway.hme_reporting.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neklaway.hme_reporting.common.data.dao.*
import com.neklaway.hme_reporting.common.data.entity.CustomerEntity
import com.neklaway.hme_reporting.common.data.entity.HMECodeEntity
import com.neklaway.hme_reporting.common.data.entity.IBAUCodeEntity
import com.neklaway.hme_reporting.common.data.entity.TimeSheetEntity
import com.neklaway.hme_reporting.common.data.entity.VisaEntity

@Database(
    entities = [CustomerEntity::class, HMECodeEntity::class, IBAUCodeEntity::class, TimeSheetEntity::class, VisaEntity::class],
    version = 1
)
abstract class RoomData : RoomDatabase() {

    abstract val customerDao: CustomerDao
    abstract val hmeCodeDao: HMECodeDao
    abstract val ibauCodeDao: IBAUCodeDao
    abstract val timeSheetDao: TimeSheetDao
    abstract val visaDao: VisaDao

    companion object {
        const val DATABASE_NAME = "db"
    }

}