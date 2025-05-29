package com.fitgymtrack.api

import com.fitgymtrack.models.ApiResponse
import com.fitgymtrack.models.ResourceLimits
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Service per le API relative agli abbonamenti
 * Implementazione Ktor multiplatform
 */
class SubscriptionApiService(private val httpClient: HttpClient) {

    /**
     * Ottiene tutti i piani di abbonamento disponibili
     */
    suspend fun getAvailablePlans(): ApiResponse<PlansResponse> {
        return httpClient.get("android_subscription_api.php") {
            parameter("action", "get_plans")
        }.body()
    }

    /**
     * Ottiene l'abbonamento attuale dell'utente
     */
    suspend fun getCurrentSubscription(): ApiResponse<SubscriptionResponse> {
        return httpClient.get("android_subscription_api.php") {
            parameter("action", "current_subscription")
        }.body()
    }

    /**
     * Controlla e aggiorna le subscription scadute
     */
    suspend fun checkExpiredSubscriptions(): ApiResponse<ExpiredCheckResponse> {
        return httpClient.get("android_subscription_api.php") {
            parameter("action", "check_expired")
        }.body()
    }

    /**
     * Verifica i limiti per un tipo di risorsa
     */
    suspend fun checkResourceLimits(resourceType: String): ApiResponse<ResourceLimits> {
        return httpClient.get("android_resource_limits_api.php") {
            parameter("resource_type", resourceType)
        }.body()
    }

    /**
     * Aggiorna il piano di abbonamento
     */
    suspend fun updatePlan(request: UpdatePlanRequest): ApiResponse<UpdatePlanResponse> {
        return httpClient.post("android_update_plan_api.php") {
            setBody(request)
        }.body()
    }
}

/**
 * Modello per la risposta di piani
 */
data class PlansResponse(
    val plans: List<SubscriptionPlanResponse>
)

/**
 * Modello per piano di abbonamento (risposta API)
 */
data class SubscriptionPlanResponse(
    val id: Int,
    val name: String,
    val price: Double,
    val billing_cycle: String,
    val max_workouts: Int? = null,
    val max_custom_exercises: Int? = null,
    val advanced_stats: Int = 0,
    val cloud_backup: Int = 0,
    val no_ads: Int = 0
)

/**
 * Modello per la risposta di abbonamento
 */
data class SubscriptionResponse(
    val subscription: ApiSubscription
)

/**
 * Modello per l'abbonamento (risposta API)
 */
data class ApiSubscription(
    val id: Int? = null,
    val user_id: Int? = null,
    val plan_id: Int,
    val plan_name: String,
    val status: String = "active",
    val price: Double,
    val max_workouts: Int? = null,
    val max_custom_exercises: Int? = null,
    val current_count: Int = 0,
    val current_custom_exercises: Int = 0,
    val advanced_stats: Int = 0,
    val cloud_backup: Int = 0,
    val no_ads: Int = 0,
    val start_date: String? = null,
    val end_date: String? = null,
    val days_remaining: Int? = null,
    val computed_status: String? = null
)

/**
 * Modello per la risposta del controllo scadenze
 */
data class ExpiredCheckResponse(
    val success: Boolean,
    val message: String,
    val updated_count: Int
)

/**
 * Modello per la richiesta di aggiornamento piano
 */
data class UpdatePlanRequest(
    val plan_id: Int
)

/**
 * Modello per la risposta di aggiornamento piano
 */
data class UpdatePlanResponse(
    val success: Boolean,
    val message: String,
    val plan_name: String
)