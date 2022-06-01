package com.kohuyn.movie.utils

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.kohuyn.movie.model.response.StatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import timber.log.Timber

fun ViewModel.getApiError(e: Throwable): StatusResponse {
    Timber.e(e)
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

fun <T> MutableStateFlow<List<UiMessage<T>>>.addMessage(message: String) {
    update { messages ->
        messages.toMutableList().apply {
            add(UiMessage(message))
        }
    }
}

fun <T> Set<UiMessage<T>>.addMessage(
    message: String,
    type: T? = null,
    code: Int = -1,
): Set<UiMessage<T>> {
    return toMutableSet().apply {
        add(UiMessage(message = message, code = code, type = type))
    }
}

fun <T> Set<UiMessage<T>>.addMessage(
    message: StatusResponse,
    type: T? = null,
): Set<UiMessage<T>> {
    return toMutableSet().apply {
        add(UiMessage(message = message.statusMessage, code = message.statusCode, type = type))
    }
}

