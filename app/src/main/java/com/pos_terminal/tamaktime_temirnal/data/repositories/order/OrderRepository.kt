package com.pos_terminal.tamaktime_temirnal.data.repositories.order

import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderToPost
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.StudentCardKey
import com.pos_terminal.tamaktime_temirnal.data.repositories.order.OrderRemoteDataSource
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val remote: OrderRemoteDataSource,
) {

    suspend fun postOrder(header: String, canteenId: Long, orderToPost: OrderToPost) =
        remote.postOrder(header, canteenId, orderToPost)

    suspend fun ordering(header: String, canteenId: Long, orderId: Long, key: StudentCardKey) =
        remote.ordering(header, canteenId, orderId, key)

}