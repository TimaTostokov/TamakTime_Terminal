package com.pos_terminal.tamaktime_temirnal.common

enum class CardState {
    INITIAL, AUTHENTICATING, AUTHENTICATING_ERROR, AUTHENTICATED,
    ORDERING
}