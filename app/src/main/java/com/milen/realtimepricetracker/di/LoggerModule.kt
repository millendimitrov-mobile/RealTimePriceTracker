package com.milen.realtimepricetracker.di

import com.milen.realtimepricetracker.domain.logger.AppLogger
import com.milen.realtimepricetracker.domain.logger.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoggerModule {

    @Binds
    @Singleton
    abstract fun bindLogger(
        appLogger: AppLogger
    ): Logger
}

