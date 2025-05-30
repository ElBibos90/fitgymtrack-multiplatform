package com.fitgymtrack.repository

import com.fitgymtrack.api.ApiClient
import com.fitgymtrack.models.*
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import com.fitgymtrack.api.isSuccessful
import com.fitgymtrack.api.statusCode
import com.fitgymtrack.api.bodyAsText

class AuthRepository {
    private val apiService = ApiClient.apiService

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                logDebug("AuthRepository", "Tentativo di login per: $username")
                val loginRequest = LoginRequest(username, password)
                val response = apiService.login(loginRequest)
                Result.success(response)
            } catch (e: Exception) {
                logError("AuthRepository", "Errore login: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun register(username: String, password: String, email: String, name: String): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(RegisterRequest(username, password, email, name))
                Result.success(response)
            } catch (e: Exception) {
                logError("AuthRepository", "Errore registrazione: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun requestPasswordReset(email: String): Result<PasswordResetResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val resetRequest = PasswordResetRequest(email)
                val response = apiService.requestPasswordReset(resetRequest)

                if (response.isSuccessful()) {
                    val responseBody = response.bodyAsText()
                    logDebug("AuthRepository", "Password reset response: $responseBody")

                    if (responseBody.contains("<b>Warning</b>") ||
                        responseBody.contains("<b>Fatal error</b>") ||
                        responseBody.contains("<br />")) {

                        logError("AuthRepository", "Risposta contiene errori PHP: $responseBody")
                        return@withContext Result.success(
                            PasswordResetResponse(
                                success = false,
                                message = "Errore del server. Contatta l'amministratore del sistema."
                            )
                        )
                    }

                    try {
                        val jsonObject = JSONObject(responseBody)
                        val success = jsonObject.optBoolean("success", false)
                        val message = jsonObject.optString("message", "")
                        val token = if (jsonObject.has("token")) jsonObject.optString("token") else null

                        Result.success(PasswordResetResponse(success, message, token))
                    } catch (jsonEx: JSONException) {
                        logError("AuthRepository", "Risposta non è JSON valido: $responseBody")
                        Result.success(
                            PasswordResetResponse(
                                success = false,
                                message = "Errore nel formato della risposta. Riprova più tardi."
                            )
                        )
                    }
                } else {
                    val errorMsg = "Errore dal server: ${response.statusCode()}"
                    logError("AuthRepository", "Reset password fallito: $errorMsg")
                    Result.success(
                        PasswordResetResponse(
                            success = false,
                            message = errorMsg
                        )
                    )
                }

            } catch (e: Exception) {
                logError("AuthRepository", "Errore richiesta reset password: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun confirmPasswordReset(token: String, code: String, newPassword: String): Result<PasswordResetConfirmResponse> {
        return withContext(Dispatchers.IO) {
            try {
                logDebug("AuthRepository", "Tentativo di reset password con: code=$code, token=$token, lunghezza password=${newPassword.length}")

                val resetConfirmRequest = PasswordResetConfirmRequest(token, code, newPassword)
                val response = apiService.confirmPasswordReset(resetConfirmRequest)

                if (response.isSuccessful()) {
                    val responseBody = response.bodyAsText()
                    logDebug("AuthRepository", "Reset password response: $responseBody")

                    if (responseBody.contains("<b>Warning</b>") ||
                        responseBody.contains("<b>Fatal error</b>") ||
                        responseBody.contains("<br />")) {

                        logError("AuthRepository", "Risposta contiene errori PHP: $responseBody")
                        return@withContext Result.success(
                            PasswordResetConfirmResponse(
                                success = false,
                                message = "Errore del server. Contatta l'amministratore del sistema."
                            )
                        )
                    }

                    try {
                        val jsonObject = JSONObject(responseBody)
                        val success = jsonObject.optBoolean("success", false)
                        val message = jsonObject.optString("message", "")

                        logDebug("AuthRepository", "Risposta parsata: success=$success, message=$message")
                        Result.success(PasswordResetConfirmResponse(success, message))
                    } catch (jsonEx: JSONException) {
                        logError("AuthRepository", "Risposta non è JSON valido: $responseBody")
                        Result.success(
                            PasswordResetConfirmResponse(
                                success = false,
                                message = "Errore nel formato della risposta. Riprova più tardi."
                            )
                        )
                    }
                } else {
                    val errorMsg = "Errore dal server: ${response.statusCode()}"
                    logError("AuthRepository", "Conferma reset password fallito: $errorMsg")
                    Result.success(
                        PasswordResetConfirmResponse(
                            success = false,
                            message = errorMsg
                        )
                    )
                }

            } catch (e: Exception) {
                logError("AuthRepository", "Errore conferma reset password: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
