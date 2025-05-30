package com.fitgymtrack.repository

import com.fitgymtrack.api.ApiClient
import com.fitgymtrack.models.*
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedbackRepository {

    private val apiService = ApiClient.feedbackApiService

    // ======== Invio feedback semplice (senza allegati) ========
    suspend fun submitFeedback(
        type: FeedbackType,
        title: String,
        description: String,
        email: String,
        severity: FeedbackSeverity = FeedbackSeverity.MEDIUM,
        deviceInfo: DeviceInfo
    ): Result<FeedbackResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val feedbackRequest = FeedbackRequest(
                    type = type.value,
                    title = title,
                    description = description,
                    email = email,
                    severity = severity.value,
                    deviceInfo = deviceInfo
                )
                val response = apiService.submitFeedback(feedbackRequest)
                Result.success(response)
            } catch (e: Exception) {
                logError("FeedbackRepository", "Errore invio feedback: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // ======== Invio feedback con allegati ========
    suspend fun submitFeedbackWithAttachments(
        type: FeedbackType,
        title: String,
        description: String,
        email: String,
        severity: FeedbackSeverity = FeedbackSeverity.MEDIUM,
        attachments: List<LocalAttachment> = emptyList(),
        deviceInfo: DeviceInfo
    ): Result<FeedbackResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val formData = formData {
                    append("type", type.value)
                    append("title", title)
                    append("description", description)
                    append("email", email)
                    append("severity", severity.value)
                    append("device_info", deviceInfo.toString())

                    attachments.forEach { attachment ->
                        append(
                            "attachments",
                            attachment.data,
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"${attachment.name}\"")
                            }
                        )
                    }
                }
                val response = apiService.submitFeedbackWithAttachments(formData)
                Result.success(response)
            } catch (e: Exception) {
                logError("FeedbackRepository", "Errore invio feedback con allegati: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // ======== Recupero feedback (solo admin) ========
    @Suppress("UNCHECKED_CAST")
    suspend fun getFeedbacks(): Result<List<Feedback>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFeedbacks()
                if (response["success"] as? Boolean == true) {
                    // TODO: converti la lista di Map in oggetti Feedback se necessario
                    Result.success(emptyList())
                } else {
                    val message = response["message"] as? String ?: "Errore sconosciuto"
                    Result.failure(Exception(message))
                }
            } catch (e: Exception) {
                logError("FeedbackRepository", "Errore recupero feedback: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // ======== Validazione feedback ========
    fun validateFeedback(
        title: String,
        description: String,
        email: String,
        attachments: List<LocalAttachment> = emptyList()
    ): ValidationResult {
        val errors = mutableListOf<String>()

        if (title.isBlank()) {
            errors.add("Il titolo è obbligatorio")
        }
        if (description.isBlank()) {
            errors.add("La descrizione è obbligatoria")
        }
        if (email.isBlank() || !email.contains("@")) {
            errors.add("Email non valida")
        }
        attachments.forEach {
            if (it.name.isBlank() || it.data.isEmpty()) {
                errors.add("Allegato non valido")
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)
