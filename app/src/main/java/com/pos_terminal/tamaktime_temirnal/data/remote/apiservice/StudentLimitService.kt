package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.limit.StudentLimit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface StudentLimitService {

    @GET(Constants.STUDENTS_LIMITS_END_POINT)
    suspend fun getStudentsLimits(
        @Header("Authorization") authHeader: String,
        @Path("student_id") studentId: Long,
    ): Response<StudentLimit>

}