package com.pos_terminal.tamaktime_temirnal.common

class CardUUIDInteractor  {

    private var _cardUuid = String()
    val cardUuid get() = _cardUuid

    fun setCardUuid(newUuid: String) {
        _cardUuid = newUuid
    }

}