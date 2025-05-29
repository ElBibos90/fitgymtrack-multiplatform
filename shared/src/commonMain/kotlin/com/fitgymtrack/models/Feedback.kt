package com.fitgymtrack.models

import kotlinx.serialization.SerialName

/**
 * Tipi di feedback disponibili
 */
enum class FeedbackType(val value: String, val displayName: String) {
    BUG("bug", "Bug Report"),
    SUGGESTION("suggestion", "Suggerimento"),
    QUESTION("question", "Domanda"),
    APPRECIATION("appreciation", "Apprezzamento")
}

/**
 * Livelli di gravità per i bug
 */
enum class FeedbackSeverity(val value: String, val displayName: String) {
    LOW("low", "Bassa"),
    MEDIUM("medium", "Media"),
    HIGH("high", "Alta"),
    CRITICAL("critical", "Critica")
}

/**
 * Stati del feedback
 */
enum class FeedbackStatus(val value: String, val displayName: String) {
    NEW("new", "Nuovo"),
    IN_PROGRESS("in_progress", "In Elaborazione"),
    CLOSED("closed", "Chiuso"),
    REJECTED("rejected", "Rifiutato")
}

/**
 * Informazioni del dispositivo raccolte automaticamente
 */
data class DeviceInfo(
    @SerialName("android_version")
    val androidVersion: String,
    @SerialName("device_model")
    val deviceModel: String,
    @SerialName("device_manufacturer")
    val deviceManufacturer: String,
    @SerialName("app_version")
    val appVersion: String,
    @SerialName("screen_size")
    val screenSize: String,
    @SerialName("api_level")
    val apiLevel: Int
)

/**
 * Richiesta per inviare feedback
 */
data class FeedbackRequest(
    val type: String,
    val title: String,
    val description: String,
    val email: String,
    val severity: String = "medium",
    @SerialName("device_info")
    val deviceInfo: DeviceInfo
)

/**
 * File attachment locale prima dell'upload
 */
data class LocalAttachment(
    val uri: String,
    val name: String,
    val size: Long,
    val mimeType: String
)

/**
 * Risposta dell'API per l'invio feedback
 */
data class FeedbackResponse(
    val success: Boolean,
    val message: String,
    @SerialName("feedback_id")
    val feedbackId: Int? = null,
    @SerialName("attachments_count")
    val attachmentsCount: Int? = null
)

/**
 * Allegato del feedback
 */
data class FeedbackAttachment(
    val id: Int,
    val filename: String,
    @SerialName("original_name")
    val originalName: String,
    @SerialName("file_size")
    val fileSize: Int
)

/**
 * Feedback completo (per visualizzazione admin)
 */
data class Feedback(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    val type: String,
    val title: String,
    val description: String,
    val email: String,
    val severity: String,
    @SerialName("device_info")
    val deviceInfo: String,
    val status: String,
    @SerialName("created_at")
    val createdAt: String,
    val username: String? = null,
    val name: String? = null,
    val attachments: List<FeedbackAttachment> = emptyList(),
    @SerialName("admin_notes")
    val adminNotes: String? = null
)