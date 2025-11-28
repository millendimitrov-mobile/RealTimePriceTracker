package com.milen.realtimepricetracker.di

import com.milen.realtimepricetracker.di.qualifiers.ApplicationScope
import com.milen.realtimepricetracker.di.qualifiers.IoDispatcher
import com.milen.realtimepricetracker.domain.dispatchers.DefaultDispatchersProvider
import com.milen.realtimepricetracker.domain.dispatchers.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DispatchersModule {

    @Binds
    @Singleton
    abstract fun bindDispatchersProvider(
        defaultDispatchersProvider: DefaultDispatchersProvider
    ): DispatchersProvider

    companion object {
        @Provides
        @Singleton
        @IoDispatcher
        fun provideIoDispatcher(
            dispatchersProvider: DispatchersProvider
        ): kotlinx.coroutines.CoroutineDispatcher = dispatchersProvider.io

        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(
            @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
        ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
    }
}

