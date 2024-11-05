package com.pos_terminal.tamaktime_temirnal.data.repositories.order

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.OrderService
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderToPost
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.StudentCardKey
import javax.inject.Inject

class OrderRemoteDataSource @Inject constructor(
    private val orderService: OrderService,
) : BaseDataSource() {

    suspend fun postOrder(header: String, canteenId: Long, orderToPost: OrderToPost) =
        getResult { orderService.postOrder(header, canteenId, orderToPost) }

    suspend fun ordering(
        header: String, canteenId: Long, orderId: Long, key: StudentCardKey,
    ) = getResult { orderService.ordering(header, canteenId, orderId, key) }

}