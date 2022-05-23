package com.kohuyn.movie.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _counter: MutableStateFlow<Int> = MutableStateFlow(0)
    val counter: StateFlow<Int> get() = _counter
    private val _startCounter: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val startCounter: StateFlow<Boolean> get() = _startCounter

    fun plusCounter() {
        _counter.update { it.plus(1) }
    }

    fun startOrStopCounter() {
        _startCounter.update { isStart -> isStart.not() }
    }
}