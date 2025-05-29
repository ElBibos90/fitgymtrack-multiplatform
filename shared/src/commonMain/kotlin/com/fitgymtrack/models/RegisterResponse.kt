package com.fitgymtrack.models

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val userId: Int? = null
)