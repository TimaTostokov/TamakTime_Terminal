package com.pos_terminal.tamaktime_temirnal.common

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Error(val throwable: Throwable, val message: String) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}