package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * Service principale per le API di autenticazione, profilo utente e gestione generale
 * Implementazione Ktor multiplatform
 */
class ApiService(private val httpClient: HttpClient) {

    // ==================== AUTH ENDPOINTS ====================

    /**
     * Login utente
     */
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return httpClient.post("auth.php") {
            parameter("action", "login")
            setBody(loginRequest)
        }.body()
    }

    /**
     * Registrazione nuovo utente
     */
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        return httpClient.post("standalone_register.php") {
            setBody(registerRequest)
        }.body()
    }

    // ==================== USER PROFILE ENDPOINTS ====================

    /**
     * Recupera il profilo utente corrente
     */
    suspend fun getUserProfile(): UserProfile {
        return httpClient.get("utente_profilo.php").body()
    }

    /**
     * Aggiorna il profilo utente
     */
    suspend fun updateUserProfile(userProfile: UserProfile): Map<String, Any> {
        return httpClient.put("utente_profilo.php") {
            setBody(userProfile)
        }.body()
    }

    // ==================== PASSWORD RESET ENDPOINTS ====================

    /**
     * Richiede il reset della password
     * Restituisce HttpResponse per gestire response non tipizzate
     */
    suspend fun requestPasswordReset(resetRequest: PasswordResetRequest): HttpResponse {
        return httpClient.post("password_reset.php") {
            parameter("action", "request")
            setBody(resetRequest)
        }
    }

    /**
     * Conferma il reset della password
     * Restituisce HttpResponse per gestire response non tipizzate
     */
    suspend fun confirmPasswordReset(resetConfirmRequest: PasswordResetConfirmRequest): HttpResponse {
        return httpClient.post("reset_simple.php") {
            parameter("action", "reset")
            setBody(resetConfirmRequest)
        }
    }

    // ==================== SUBSCRIPTION ENDPOINTS ====================

    /**
     * Recupera l'abbonamento corrente
     */
    suspend fun getCurrentSubscription(): Map<String, Any> {
        return httpClient.get("subscription_api.php") {
            parameter("action", "current_subscription")
        }.body()
    }

    /**
     * Verifica i limiti delle risorse
     */
    suspend fun checkResourceLimits(resourceType: String): Map<String, Any> {
        return httpClient.get("subscription_api.php") {
            parameter("resource_type", resourceType)
            parameter("action", "check_limits")
        }.body()
    }

    /**
     * Aggiorna il piano di abbonamento
     */
    suspend fun updatePlan(request: Map<String, Any>): Map<String, Any> {
        return httpClient.post("subscription_api.php") {
            parameter("action", "update_plan")
            setBody(request)
        }.body()
    }

    // ==================== PAYMENT ENDPOINTS ====================

    /**
     * Inizializza un pagamento PayPal
     */
    suspend fun initializePayment(paymentRequest: PaymentRequest): PaymentResponse {
        return httpClient.post("paypal_payment.php") {
            setBody(paymentRequest)
        }.body()
    }

    // ==================== FEEDBACK ENDPOINTS ====================

    /**
     * Invia un feedback
     */
    suspend fun submitFeedback(feedbackRequest: FeedbackRequest): FeedbackResponse {
        return httpClient.post("feedback_api.php") {
            setBody(feedbackRequest)
        }.body()
    }

    /**
     * Recupera tutti i feedback (solo per admin)
     */
    suspend fun getFeedbacks(): Map<String, Any> {
        return httpClient.get("feedback_api.php").body()
    }

    /**
     * Aggiorna lo stato di un feedback (solo per admin)
     */
    suspend fun updateFeedbackStatus(request: Map<String, Any>): Map<String, Any> {
        return httpClient.post("feedback_api.php") {
            setBody(request)
        }.body()
    }

    /**
     * Aggiorna le note admin di un feedback (solo per admin)
     */
    suspend fun updateFeedbackNotes(request: Map<String, Any>): Map<String, Any> {
        return httpClient.post("feedback_api.php") {
            setBody(request)
        }.body()
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Estrae il testo dalla risposta HTTP per endpoint che restituiscono plain text
     */
    suspend fun HttpResponse.bodyAsText(): String = this.bodyAsText()

    /**
     * Verifica se la risposta HTTP è successful
     */
    fun HttpResponse.isSuccessful(): Boolean = this.status.isSuccess()

    /**
     * Ottiene lo status code della risposta
     */
    fun HttpResponse.statusCode(): Int = this.status.value
}