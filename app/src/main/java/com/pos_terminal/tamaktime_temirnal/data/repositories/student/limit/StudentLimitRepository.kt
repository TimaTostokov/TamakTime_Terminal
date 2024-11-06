package com.pos_terminal.tamaktime_temirnal.data.repositories.student.limit

import javax.inject.Inject

class StudentLimitRepository @Inject constructor(
    private val studentLimitRemoteDataSource: StudentLimitRemoteDataSource,
) {
    suspend fun getStudentLimit(header: String, studentId: Long) =
        studentLimitRemoteDataSource.getStudentLimit(header, studentId)
}