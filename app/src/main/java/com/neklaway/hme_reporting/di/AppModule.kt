package com.neklaway.hme_reporting.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.neklaway.hme_reporting.common.data.RoomData
import com.neklaway.hme_reporting.common.data.dao.*
import com.neklaway.hme_reporting.common.data.entity.StringListConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(app: Application): RoomData {
        return Room.databaseBuilder(
            app,
            RoomData::class.java,
            RoomData.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(db: RoomData): CustomerDao {
        return db.customerDao
    }

    @Provides
    @Singleton
    fun provideHMECodeDao(db: RoomData): HMECodeDao {
        return db.hmeCodeDao
    }

    @Provides
    @Singleton
    fun provideIBAUCodeDao(db: RoomData): IBAUCodeDao {
        return db.ibauCodeDao
    }

    @Provides
    @Singleton
    fun provideTimeSheetDao(db: RoomData): TimeSheetDao {
        return db.timeSheetDao
    }

    @Provides
    @Singleton
    fun provideVisaDao(db: RoomData): VisaDao {
        return db.visaDao
    }

    @Provides
    @Singleton
    fun provideCarMileageDao(db: RoomData): CarMileageDao {
        return db.carMileageDao
    }

    @Provides
    @Singleton
    fun provideCurrencyExchangeDao(db: RoomData): CurrencyExchangeDao {
        return db.currencyExchangeDao
    }

    @Provides
    @Singleton
    fun provideExpanseDao(db: RoomData): ExpanseDao {
        return db.expanseDao
    }

    @Provides
    @Singleton
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }


}