package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.STUDENTS_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface StudentService {

    @GET(STUDENTS_END_POINT)
    suspend fun getStudentBySchoolIdAndCardUUID(
        @Header("Authorization") authHeader: String,
        @Path("school_id") schoolId: Long,
        @Path("card_uuid") cardUuid: String,
    ): Response<Student>

}