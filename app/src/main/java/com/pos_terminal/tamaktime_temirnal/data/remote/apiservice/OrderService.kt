package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.ORDER_END_POINT
import com.pos_terminal.tamaktime_temirnal.common.Constants.ORDER_ID_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderResponse
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderToPost
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.StudentCardKey
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    @POST(ORDER_END_POINT)
    suspend fun postOrder(
        @Header("Authorization") authHeader: String,
        @Path("canteenId") canteenId: Long,
        @Body orderToPost: OrderToPost,
    ): Response<OrderResponse>

    @POST(ORDER_ID_END_POINT)
    suspend fun ordering(
        @Header("Authorization") authHeader: String,
        @Path("canteenId") canteenId: Long,
        @Path("orderId") orderId: Long,
        @Body key: StudentCardKey,
    ) : Response<OrderResponse>

}