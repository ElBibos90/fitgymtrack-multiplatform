package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*

class ApiService(private val httpClient: HttpClient) {

    // ======== AUTH EXAMPLE ========
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return httpClient.post("auth.php") {
            parameter("action", "login")
            setBody(loginRequest)
        }.body()
    }

    // ======== FEEDBACK API ========
    suspend fun submitFeedback(feedbackRequest: FeedbackRequest): FeedbackResponse {
        return httpClient.post("feedback_api.php") {
            setBody(feedbackRequest)
        }.body()
    }

    suspend fun submitFeedbackWithAttachments(formData: MultiPartFormDataContent): FeedbackResponse {
        return httpClient.post("feedback_api.php") {
            setBody(formData)
        }.body()
    }

    suspend fun getFeedbacks(): Map<String, Any> {
        return httpClient.get("feedback_api.php").body()
    }
}
    // ======== HTTP EXTENSIONS ========
    suspend fun HttpResponse.bodyAsText(): String = this.bodyAsText()
    fun HttpResponse.isSuccessful(): Boolean = this.status.isSuccess()
    fun HttpResponse.statusCode(): Int = this.status.value

