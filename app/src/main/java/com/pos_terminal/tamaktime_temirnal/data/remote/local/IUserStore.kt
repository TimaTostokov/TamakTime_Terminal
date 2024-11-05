package com.pos_terminal.tamaktime_temirnal.data.remote.local

import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import com.pos_terminal.tamaktime_temirnal.data.remote.model.user.User
import kotlinx.coroutines.flow.Flow

interface IUserStore {
    fun flowHasCredentials(): Flow<Boolean>
    fun flowCredentials(): Flow<String?>
    suspend fun clear()
    suspend fun saveUser(user: User)
    fun flowCanteenId(): Flow<Long?>
    suspend fun getCredentials(): String?
    suspend fun getCanteenId(): Long?
    suspend fun getSchoolId(): Long?
    suspend fun saveCardUuid(cardUuid: String)
    suspend fun getStudentId(): Long?
    suspend fun saveStudentId(student: Student)
}