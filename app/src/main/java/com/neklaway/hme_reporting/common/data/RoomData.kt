package com.neklaway.hme_reporting.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neklaway.hme_reporting.feature_time_sheet.data.dao.CustomerDao
import com.neklaway.hme_reporting.feature_time_sheet.data.dao.HMECodeDao
import com.neklaway.hme_reporting.feature_time_sheet.data.dao.IBAUCodeDao
import com.neklaway.hme_reporting.feature_time_sheet.data.dao.TimeSheetDao
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.CustomerEntity
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.HMECodeEntity
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.IBAUCodeEntity
import com.neklaway.hme_reporting.feature_time_sheet.data.entity.TimeSheetEntity
import com.neklaway.hme_reporting.feature_visa.data.dao.VisaDao
import com.neklaway.hme_reporting.feature_visa.data.entity.VisaEntity

@Database(entities = [CustomerEntity::class, HMECodeEntity::class, IBAUCodeEntity::class, TimeSheetEntity::class,VisaEntity::class],
    version = 1)
abstract class RoomData : RoomDatabase() {

    abstract val customerDao: CustomerDao
    abstract val hmeCodeDao: HMECodeDao
    abstract val ibauCodeDao: IBAUCodeDao
    abstract val timeSheetDao: TimeSheetDao
    abstract val visaDao: VisaDao

    companion object{
        const val DATABASE_NAME = "db"
    }

}