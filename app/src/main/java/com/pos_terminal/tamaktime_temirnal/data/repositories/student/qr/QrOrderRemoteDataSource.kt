package com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.QRService
import javax.inject.Inject

class QrOrderRemoteDataSource @Inject constructor(
    private val qrService: QRService,
) : BaseDataSource() {

    suspend fun getStudentByQR(
        header: String,
        cardUuid: String,
    ) = getResult {
        qrService.getStudentByQR(header, cardUuid)
    }

}