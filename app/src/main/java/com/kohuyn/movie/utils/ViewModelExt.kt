package com.kohuyn.movie.utils

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.kohuyn.movie.model.response.StatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException

fun ViewModel.getApiError(e: Throwable): StatusResponse {
    return if (e is HttpException) {
        try {
            Gson().fromJson(e.response()?.errorBody()?.string(), StatusResponse::class.java)
        } catch (e: JsonParseException) {
            StatusResponse(-1, "Json Parser Exception")
        }
    } else {
        StatusResponse(-1, e.message ?: "Unknown message")
    }
}

fun MutableStateFlow<List<UiMessage>>.addMessage(message: String) {
    update { messages ->
        messages.toMutableList().apply {
            add(UiMessage(message))
        }
    }
}