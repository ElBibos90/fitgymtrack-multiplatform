package com.fitgymtrack.api

import com.fitgymtrack.models.ApiResponse
import com.fitgymtrack.models.AppVersionResponse
import com.fitgymtrack.models.MarkMessageReadRequest
import com.fitgymtrack.models.UserMessagesResponse
import com.fitgymtrack.platform.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * API Service per gestione notifiche e version checking
 * Implementazione Ktor multiplatform
 */
class NotificationApiService(private val httpClient: HttpClient) {

    /**
     * Verifica se è disponibile un aggiornamento dell'app
     */
    suspend fun checkAppVersion(request: AppVersionCheckRequest): AppVersionResponse {
        return httpClient.post("app_version_check.php") {
            setBody(request)
        }.body()
    }

    /**
     * Recupera messaggi per l'utente corrente
     */
    suspend fun getUserMessages(
        includeRead: Boolean = false,
        limit: Int = 50
    ): UserMessagesResponse {
        return httpClient.get("user_messages.php") {
            parameter("include_read", includeRead)
            parameter("limit", limit)
        }.body()
    }

    /**
     * Marca un messaggio come letto
     */
    suspend fun markMessageAsRead(request: MarkMessageReadRequest): ApiResponse<String> {
        return httpClient.post("user_messages.php") {
            setBody(request)
        }.body()
    }

    /**
     * Marca tutti i messaggi come letti
     */
    suspend fun markAllMessagesAsRead(): ApiResponse<String> {
        return httpClient.post("user_messages.php").body()
    }

    /**
     * Elimina un messaggio (solo se permesso)
     */
    suspend fun deleteMessage(messageId: String): ApiResponse<String> {
        return httpClient.delete("user_messages.php") {
            parameter("message_id", messageId)
        }.body()
    }

    /**
     * Invia un messaggio diretto (solo admin)
     */
    suspend fun sendDirectMessage(request: SendMessageRequest): ApiResponse<String> {
        return httpClient.post("admin_messages.php") {
            setBody(request)
        }.body()
    }

    /**
     * Recupera statistiche messaggi (solo admin)
     */
    suspend fun getMessageStats(): ApiResponse<MessageStats> {
        return httpClient.get("admin_messages.php").body()
    }
}

/**
 * Richiesta per check versione app
 */
data class AppVersionCheckRequest(
    val current_version: String,
    val current_version_code: Int,
    val platform: String = "android",
    val device_info: DeviceInfo? = null
)

/**
 * Informazioni dispositivo per analytics
 */
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val android_version: String,
    val app_language: String = "it"
)

/**
 * Richiesta per inviare messaggio diretto (admin)
 */
data class SendMessageRequest(
    val target_user_id: Int? = null,
    val title: String,
    val message: String,
    val is_markdown: Boolean = false,
    val priority: String = "NORMAL",
    val expires_at: String? = null,
    val image_url: String? = null,
    val action_button: ActionButtonRequest? = null
)

/**
 * Bottone azione per messaggi
 */
data class ActionButtonRequest(
    val text: String,
    val action: String,
    val data: String
)

/**
 * Statistiche messaggi per admin
 */
data class MessageStats(
    val total_messages: Int,
    val total_users: Int,
    val messages_sent_today: Int,
    val messages_sent_this_week: Int,
    val read_rate: Double,
    val recent_messages: List<RecentMessage>
)

/**
 * Messaggio recente per admin dashboard
 */
data class RecentMessage(
    val id: String,
    val title: String,
    val sent_at: String,
    val recipient_count: Int,
    val read_count: Int,
    val read_rate: Double
)