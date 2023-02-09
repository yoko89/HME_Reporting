package com.neklaway.hme_reporting.di


import com.neklaway.hme_reporting.common.data.repository_impl.*
import com.neklaway.hme_reporting.common.domain.repository.*
import com.neklaway.hme_reporting.feature_settings.data.repository_impl.BackupRepositoryImpl
import com.neklaway.hme_reporting.feature_settings.data.repository_impl.RestoreRepositoryImpl
import com.neklaway.hme_reporting.feature_settings.data.repository_impl.SettingsRepositoryImpl
import com.neklaway.hme_reporting.feature_settings.domain.repository.BackupRepository
import com.neklaway.hme_reporting.feature_settings.domain.repository.RestoreRepository
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideCustomerRepository(customerRepositoryImpl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    @Singleton
    abstract fun provideHMECodeRepository(hmeCodeRepositoryImpl: HMECodeRepositoryImpl): HMECodeRepository

    @Binds
    @Singleton
    abstract fun provideIBAUCodeRepository(ibauCodeRepositoryImpl: IBAUCodeRepositoryImpl): IBAUCodeRepository

    @Binds
    @Singleton
    abstract fun provideTimeSheetRepository(timeSheetRepositoryImpl: TimeSheetRepositoryImpl): TimeSheetRepository

    @Binds
    @Singleton
    abstract fun provideVisaRepository(visaRepositoryImpl: VisaRepositoryImpl): VisaRepository

    @Binds
    @Singleton
    abstract fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun provideSavedDataRepository(savedDataRepository: SavedDataRepositoryImpl): SavedDataRepository

    @Binds
    @Singleton
    abstract fun provideBackupRepository(backupRepositoryImpl: BackupRepositoryImpl): BackupRepository

    @Binds
    @Singleton
    abstract fun provideRestoreRepository(restoreRepositoryImpl: RestoreRepositoryImpl): RestoreRepository

    @Binds
    @Singleton
    abstract fun provideCarMileageRepository(carMileageRepositoryImpl: CarMileageRepositoryImpl): CarMileageRepository

}