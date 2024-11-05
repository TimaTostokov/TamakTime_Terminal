package com.pos_terminal.tamaktime_temirnal.common

import android.graphics.Color

var inputStates = arrayOf(
    intArrayOf(android.R.attr.state_focused),
)

var states = arrayOf(
    intArrayOf(android.R.attr.state_enabled),
    intArrayOf(-android.R.attr.state_enabled),
    intArrayOf(-android.R.attr.state_checked),
    intArrayOf(android.R.attr.state_pressed)
)

var colors = intArrayOf(
    Color.BLACK,
    Color.RED,
    Color.GREEN,
    Color.BLUE
)