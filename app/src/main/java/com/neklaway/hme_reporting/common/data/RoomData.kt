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
        CarMileageEntity::class,
        ExpanseEntity::class,
        CurrencyExchangeEntity::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, 3)]
)
abstract class RoomData : RoomDatabase() {

    abstract val customerDao: CustomerDao
    abstract val hmeCodeDao: HMECodeDao
    abstract val ibauCodeDao: IBAUCodeDao
    abstract val timeSheetDao: TimeSheetDao
    abstract val visaDao: VisaDao
    abstract val carMileageDao: CarMileageDao
    abstract val currencyExchangeDao: CurrencyExchangeDao
    abstract val expanseDao: ExpanseDao

    companion object {
        const val DATABASE_NAME = "db"
    }

}