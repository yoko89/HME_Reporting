package com.neklaway.hme_reporting.di


import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.feature_settings.data.repository_impl.SettingsRepositoryImpl
import com.neklaway.hme_reporting.feature_time_sheet.data.repository_impl.*
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.*
import com.neklaway.hme_reporting.feature_visa.data.repository_impl.VisaRepositoryImpl
import com.neklaway.hme_reporting.feature_visa.domain.repository.VisaRepository
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

}