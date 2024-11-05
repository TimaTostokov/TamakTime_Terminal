package com.pos_terminal.tamaktime_temirnal.data.repositories.student

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.StudentService
import javax.inject.Inject

class StudentRemoteDataSource @Inject constructor(
    private val studentService: StudentService,
) : BaseDataSource() {

    suspend fun getStudentBySchoolIdAndCardUUID(
        header: String,
        schoolId: Long,
        cardUuid: String,
    ) = getResult {
        studentService.getStudentBySchoolIdAndCardUUID(header, schoolId, cardUuid)
    }

}