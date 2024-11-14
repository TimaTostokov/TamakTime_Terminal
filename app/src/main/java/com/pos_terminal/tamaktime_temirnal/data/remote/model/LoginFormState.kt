package com.pos_terminal.tamaktime_temirnal.data.remote.model

data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false,
    val isLoading: Boolean = false,
)