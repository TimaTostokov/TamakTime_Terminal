package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.QR_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.qr_order.QROrderItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface QRService {

    @GET(QR_END_POINT)
    suspend fun getStudentByQR(
        @Header("Authorization") authHeader: String,
        @Path("card_uuid") cardUuid: String,
    ): Response<List<QROrderItem>>

}