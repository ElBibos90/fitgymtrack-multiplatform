package com.fitgymtrack.api

import com.fitgymtrack.platform.logError
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.utils.SessionManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * Client API centralizzato per tutte le chiamate di rete
 * Implementazione Ktor multiplatform
 */
object ApiClient {

    // Configuration constants
    //private const val BASE_URL = "http://192.168.1.113/api/" // Per emulatore che punta a localhost
    private const val BASE_URL = "https://fitgymtrack.com/api/" // Per il server remoto

    private const val CONNECT_TIMEOUT = 30
    private const val REQUEST_TIMEOUT = 30

    private lateinit var sessionManager: SessionManager

    // Inizializza il sessionManager
    fun initialize(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
    }

    // Configurazione JSON
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    // HttpClient configurato
    private val httpClient by lazy {
        HttpClient {
            // Serialization
            install(ContentNegotiation) {
                json(json)
            }

            // Logging
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        logDebug("HttpClient", message)
                    }
                }
                level = LogLevel.BODY
            }

            // Timeouts
            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT.seconds.inWholeMilliseconds
                requestTimeoutMillis = REQUEST_TIMEOUT.seconds.inWholeMilliseconds
                socketTimeoutMillis = REQUEST_TIMEOUT.seconds.inWholeMilliseconds
            }

            // Default request configuration
            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
            }

            // Auth interceptor
            install(Auth) {
                bearer {
                    loadTokens {
                        // Se sessionManager non è inizializzato, nessun token
                        if (!::sessionManager.isInitialized) {
                            return@loadTokens null
                        }

                        try {
                            val token = sessionManager.getAuthToken().first()
                            if (!token.isNullOrEmpty()) {
                                BearerTokens(token, "")
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            logError("ApiClient", "Errore recupero token: ${e.message}")
                            null
                        }
                    }

                    refreshTokens {
                        // TODO: Implementare refresh token logic se necessario
                        null
                    }
                }
            }

            // Response validation & error handling
            install(HttpCallValidator) {
                handleResponseExceptionWithRequest { exception, request ->
                    logError("ApiClient", "Errore HTTP: ${exception.message} per ${request.url}")
                }

                validateResponse { response ->
                    // Custom response validation logic here if needed
                }
            }
        }
    }

    // Factory functions per i servizi API
    private inline fun <reified T> createService(): T where T : Any {
        return when (T::class) {
            ApiService::class -> ApiService(httpClient) as T
            WorkoutApiService::class -> WorkoutApiService(httpClient) as T
            ActiveWorkoutApiService::class -> ActiveWorkoutApiService(httpClient, BASE_URL) as T
            UserExerciseApiService::class -> UserExerciseApiService(httpClient) as T
            ExerciseApiService::class -> ExerciseApiService(httpClient) as T
            WorkoutHistoryApiService::class -> WorkoutHistoryApiService(httpClient) as T
            PaymentApiService::class -> PaymentApiService(httpClient) as T
            SubscriptionApiService::class -> SubscriptionApiService(httpClient) as T
            StatsApiService::class -> StatsApiService(httpClient) as T
            FeedbackApiService::class -> FeedbackApiService(httpClient) as T
            NotificationApiService::class -> NotificationApiService(httpClient) as T
            else -> throw IllegalArgumentException("Unknown service type: ${T::class}")
        }
    }

    // Servizi API disponibili (lazy initialization)
    val apiService: ApiService by lazy {
        createService<ApiService>()
    }

    val workoutApiService: WorkoutApiService by lazy {
        createService<WorkoutApiService>()
    }

    val activeWorkoutApiService: ActiveWorkoutApiService by lazy {
        createService<ActiveWorkoutApiService>()
    }

    val userExerciseApiService: UserExerciseApiService by lazy {
        createService<UserExerciseApiService>()
    }

    val exerciseApiService: ExerciseApiService by lazy {
        createService<ExerciseApiService>()
    }

    val workoutHistoryApiService: WorkoutHistoryApiService by lazy {
        createService<WorkoutHistoryApiService>()
    }

    val paymentApiService: PaymentApiService by lazy {
        createService<PaymentApiService>()
    }

    val subscriptionApiService: SubscriptionApiService by lazy {
        createService<SubscriptionApiService>()
    }

    val statsApiService: StatsApiService by lazy {
        createService<StatsApiService>()
    }

    val feedbackApiService: FeedbackApiService by lazy {
        createService<FeedbackApiService>()
    }

    val notificationApiService: NotificationApiService by lazy {
        createService<NotificationApiService>()
    }

    // Utility functions
    fun closeClient() {
        httpClient.close()
    }

    // Per debugging/testing
    fun getBaseUrl(): String = BASE_URL
}