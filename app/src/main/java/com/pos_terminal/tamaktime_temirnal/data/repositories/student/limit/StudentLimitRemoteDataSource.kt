package com.pos_terminal.tamaktime_temirnal.data.repositories.student.limit

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.StudentLimitService
import javax.inject.Inject

class StudentLimitRemoteDataSource @Inject constructor(
    private val studentLimitService: StudentLimitService
) : BaseDataSource() {

    suspend fun getStudentLimit(
        header: String,
        studentId: Long
    ) = getResult {
        studentLimitService.getStudentsLimits(header, studentId)
    }

}