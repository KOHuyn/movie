package com.kohuyn.movie.model.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenResponse(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("request_token")
    val requestToken: String?
)