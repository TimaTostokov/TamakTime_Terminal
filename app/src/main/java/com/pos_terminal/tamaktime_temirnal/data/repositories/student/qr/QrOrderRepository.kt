package com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr

import javax.inject.Inject

class QrOrderRepository @Inject constructor(
    private val remote: QrOrderRemoteDataSource
) {
    suspend fun getStudentByQR(header: String, cardUuid: String) =
        remote.getStudentByQR(header, cardUuid)
}