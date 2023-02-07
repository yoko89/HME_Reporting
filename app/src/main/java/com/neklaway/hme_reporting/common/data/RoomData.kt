package com.neklaway.hme_reporting.common.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.neklaway.hme_reporting.common.data.dao.*
import com.neklaway.hme_reporting.common.data.entity.*

@Database(
    entities = [CustomerEntity::class,
        HMECodeEntity::class,
        IBAUCodeEntity::class,
        TimeSheetEntity::class,
        VisaEntity::class,
        CarMileageEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1 , to = 2)]
)
abstract class RoomData : RoomDatabase() {

    abstract val customerDao: CustomerDao
    abstract val hmeCodeDao: HMECodeDao
    abstract val ibauCodeDao: IBAUCodeDao
    abstract val timeSheetDao: TimeSheetDao
    abstract val visaDao: VisaDao
    abstract val carMileageDao: CarMileageDao

    companion object {
        const val DATABASE_NAME = "db"
    }

}