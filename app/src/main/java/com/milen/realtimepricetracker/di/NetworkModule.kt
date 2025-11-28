package com.milen.realtimepricetracker.di

import com.milen.realtimepricetracker.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import com.milen.realtimepricetracker.domain.logger.Logger as AppLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val TAG = "HttpClient"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        logger: AppLogger,
    ): HttpClient =
        HttpClient(CIO) {
            install(WebSockets)

            // JSON serialization for HTTP requests
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                this.logger = object : KtorLogger {
                    override fun log(message: String) {
                        logger.logEvent(message, TAG)
                    }
                }
                level = if (BuildConfig.DEBUG) {
                    LogLevel.ALL
                } else {
                    LogLevel.NONE
                }
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 60_000
                requestTimeoutMillis = 60_000
            }

            install(HttpCallValidator) {
                handleResponseExceptionWithRequest { exception, _ ->
                    logger.logError("HTTP error: ${exception.message}", exception, TAG)
                }
                validateResponse { response ->
                    val statusCode = response.status
                    if (statusCode.value >= 400) {
                        logger.logError(
                            "HTTP error response: $statusCode - ${statusCode.description}",
                            null,
                            TAG
                        )
                    }
                }
            }

            expectSuccess = false
        }
}
