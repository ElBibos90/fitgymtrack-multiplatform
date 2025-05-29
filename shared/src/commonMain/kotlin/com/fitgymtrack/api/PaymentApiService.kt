package com.fitgymtrack.api

import com.fitgymtrack.models.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Service per le API di pagamento
 * Implementazione Ktor multiplatform
 */
class PaymentApiService(private val httpClient: HttpClient) {

    /**
     * Inizializza un pagamento PayPal
     */
    suspend fun initializePayment(paymentRequest: PaymentRequest): ApiResponse<PaymentResponse> {
        return httpClient.post("android_paypal_payment.php") {
            setBody(paymentRequest)
        }.body()
    }

    /**
     * Verifica lo stato di un pagamento
     */
    suspend fun checkPaymentStatus(orderId: String): ApiResponse<PaymentStatus> {
        return httpClient.get("android_payment_status.php") {
            parameter("order_id", orderId)
        }.body()
    }
}

/**
 * Modello per la richiesta di pagamento
 */
data class PaymentRequest(
    val amount: Double,
    val type: String = "subscription",
    val plan_id: Int? = null,
    val message: String? = null,
    val display_name: Boolean = true
)

/**
 * Modello per la risposta di pagamento
 */
data class PaymentResponse(
    val order_id: String,
    val paypal_order_id: String,
    val approval_url: String
)

/**
 * Modello per lo stato di un pagamento
 */
data class PaymentStatus(
    val success: Boolean,
    val order_id: String,
    val paypal_order_id: String,
    val status: String,
    val amount: Double,
    val type: String,
    val plan_id: Int?,
    val paypal_status: String?
)