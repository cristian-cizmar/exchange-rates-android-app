package com.cristiancizmar.exchangerates.di

import android.content.Context
import android.content.SharedPreferences
import com.cristiancizmar.exchangerates.App
import com.cristiancizmar.exchangerates.data.AppSharedPreferences
import com.cristiancizmar.exchangerates.data.ExchangeRepository
import com.cristiancizmar.exchangerates.data.PreferencesRepository
import com.cristiancizmar.exchangerates.data.rest.ExchangeService
import com.cristiancizmar.exchangerates.util.PACKAGE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExchangeService(): ExchangeService {
        return ExchangeService()
    }

    @Provides
    @Singleton
    fun provideExchangeRepository(exchangeService: ExchangeService): ExchangeRepository {
        return ExchangeRepository(exchangeService)
    }

    @Provides
    @Singleton
    fun provideAppSharedPreferences(sharedPreferences: SharedPreferences): AppSharedPreferences {
        return AppSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(app: App): SharedPreferences {
        return app.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideApp() = App.appInstance()

    @Provides
    @Singleton
    fun providePreferencesRepository(appSharedPreferences: AppSharedPreferences): PreferencesRepository {
        return PreferencesRepository(appSharedPreferences)
    }
}