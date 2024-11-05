package com.pos_terminal.tamaktime_temirnal.data.repositories.student

import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val remote: StudentRemoteDataSource
) {
    suspend fun getStudentBySchoolIdAndCardUUID(header: String, schoolId: Long, cardUuid: String) =
        remote.getStudentBySchoolIdAndCardUUID(header, schoolId, cardUuid)
}