package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

/**
 * Service per l'API del feedback
 * Implementazione Ktor multiplatform
 */
class FeedbackApiService(private val httpClient: HttpClient) {

    /**
     * Invia un feedback senza allegati
     */
    suspend fun submitFeedback(feedbackRequest: FeedbackRequest): FeedbackResponse {
        return httpClient.post("feedback_api.php") {
            setBody(feedbackRequest)
        }.body()
    }

    /**
     * Invia un feedback con allegati (multipart)
     */
    suspend fun submitFeedbackWithAttachments(
        type: String,
        title: String,
        description: String,
        email: String,
        severity: String,
        deviceInfo: String,
        attachments: List<ByteArray>
    ): FeedbackResponse {
        return httpClient.submitFormWithBinaryData(
            url = "feedback_api.php",
            formData = formData {
                append("type", type)
                append("title", title)
                append("description", description)
                append("email", email)
                append("severity", severity)
                append("device_info", deviceInfo)

                attachments.forEachIndexed { index, attachment ->
                    append(
                        key = "attachments[]",
                        value = attachment,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "application/octet-stream")
                            append(HttpHeaders.ContentDisposition, "filename=\"attachment_$index\"")
                        }
                    )
                }
            }
        ).body()
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
}