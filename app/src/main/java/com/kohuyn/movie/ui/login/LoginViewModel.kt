package com.kohuyn.movie.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.kohuyn.movie.model.response.AccountDetailResponse
import com.kohuyn.movie.model.response.CreateSessionResponse
import com.kohuyn.movie.model.response.TokenResponse
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.StorageCache
import com.kohuyn.movie.utils.UiMessage
import com.kohuyn.movie.utils.addMessage
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _messages: MutableStateFlow<List<UiMessage>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<UiMessage>> get() = _messages

    var onLoginSuccess: () -> Unit = {}

    private fun validateWithLoginFlow(
        userName: String,
        password: String,
        requestToken: String?
    ): Flow<TokenResponse> {
        val body = JsonObject().apply {
            addProperty("username", userName)
            addProperty("password", password)
            addProperty("request_token", requestToken)
        }
        return flow { emit(RetrofitUtils.apiService.validateWithLogin(body)) }
    }

    private fun createSessionFlow(requestToken: String): Flow<CreateSessionResponse> {
        return flow {
            emit(
                RetrofitUtils.apiService.createSession(
                    JsonObject().apply {
                        addProperty("request_token", requestToken)
                    })
            )
        }
    }

    private fun getAccountDetail(sessionId: String): Flow<AccountDetailResponse> {
        return flow { emit(RetrofitUtils.apiService.getAccountDetail(sessionId)) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(userName: String, password: String) {
        viewModelScope.launch {
            flow {
                emit(RetrofitUtils.apiService.getTokenNew())
            }
                .filter { tokenResponse ->
                    tokenResponse.success == true
                }
                .map { tokenResponse ->
                    tokenResponse.requestToken
                }
                .flatMapLatest { requestToken ->
                    validateWithLoginFlow(
                        userName = userName,
                        password = password,
                        requestToken = requestToken
                    )
                }
                .filter { tokenResponse ->
                    tokenResponse.success == true
                }
                .mapNotNull { tokenResponse ->
                    tokenResponse.requestToken
                }
                .onEach { requestToken ->
                    StorageCache.token = requestToken
                }
                .flatMapLatest { requestToken ->
                    createSessionFlow(requestToken)
                }
                .filter { sessionResponse ->
                    sessionResponse.success == true
                }
                .mapNotNull { sessionResponse ->
                    sessionResponse.sessionId
                }
                .onEach { sessionId ->
                    StorageCache.sessionId = sessionId
                }
                .flatMapLatest { sessionId ->
                    getAccountDetail(sessionId)
                }
                .onEach { accountDetail ->
                    StorageCache.accountId = accountDetail.id
                }
                .onStart { _isLoading.update { true } }
                .onCompletion { _isLoading.update { false } }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.addMessage(statusResponse.statusMessage)
                }
                .collect {
                    onLoginSuccess()
                }
        }
    }

    fun setMessageShown(message: String) {
        _messages.update { messages ->
            messages.filterNot { it.message == message }
        }
    }
}