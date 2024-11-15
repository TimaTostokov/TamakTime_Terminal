package com.pos_terminal.tamaktime_temirnal.data.remote.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import com.pos_terminal.tamaktime_temirnal.data.remote.model.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class UserStoreImpl @Inject constructor(
    @ApplicationContext context: Context,
) : IUserStore {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val _hasCredentials = MutableStateFlow(false)
    private val _credentials = MutableStateFlow<String?>(null)
    private val _canteenId = MutableStateFlow<Long?>(null)

    init {
        _hasCredentials.value = sharedPreferences.getString("credentials", "")!!.isNotEmpty()
        _credentials.value = sharedPreferences.getString("credentials", null)
        _canteenId.value = sharedPreferences.getLong("canteenId", -1L).takeIf { it != -1L }
    }

    override fun flowHasCredentials(): StateFlow<Boolean> = _hasCredentials.asStateFlow()

    override fun flowCredentials(): StateFlow<String?> = _credentials.asStateFlow()

    override fun flowCanteenId(): StateFlow<Long?> = _canteenId.asStateFlow()

    override suspend fun getCredentials(): String? {
        return sharedPreferences.getString("credentials", null)
    }

    override suspend fun getCanteenId(): Long? {
        val canteenId = sharedPreferences.getLong("canteenId", -1L)
        return if (canteenId != -1L) canteenId else null
    }

    override suspend fun getSchoolId(): Long? {
        val schoolId = sharedPreferences.getLong("schoolId", -1L)
        return if (schoolId != -1L) schoolId else null
    }

    override suspend fun getStudentId(): Long? {
        val studentId = sharedPreferences.getLong("studentId", -1L)
        return if (studentId != -1L) studentId else null
    }

    override suspend fun saveCardUuid(cardUuid: String) {
        sharedPreferences.edit().apply {
            putString("card_uuid", cardUuid)
        }.apply()
    }

    override suspend fun clear() {
        sharedPreferences.edit().clear().apply()
        _hasCredentials.value = false
        _credentials.value = null
        _canteenId.value = null
    }

    override suspend fun saveUser(user: User) {
        sharedPreferences.edit().apply {
            putString("credentials", user.credentials)
            putLong("id", user.id)
            putString("phone", user.phone)
            putString("firstName", user.firstName)
            putString("lastName", user.lastName)
            putString("photo", user.photo ?: "")
            putString("email", user.email ?: "")
            putString("birthDate", user.birthDate ?: "")
            putString("canteenRole", user.canteens.first().role)
            user.canteens.first().canteen?.let { putLong("canteenId", it.id) }
            putString("canteenName", user.canteens.first().canteen?.name)
            putString("canteenAvatar", user.canteens.first().canteen?.avatar)
            user.canteens.first().canteen?.school?.let { putLong("schoolId", it.id) }
        }.apply()

        _hasCredentials.value = true
        _credentials.value = user.credentials
        _canteenId.value = user.canteens.first().canteen?.id
    }

    override suspend fun saveStudentId(student: Student) {
        student.let {
            it.id?.let { id ->
                sharedPreferences.edit().apply {
                    putLong("studentId", id.toLong())
                    Log.d("UserStoreImpl", "Saved Student ID: $id")

                }.apply()
            } ?: Log.e("UserStoreImpl", "Student ID is null.")
        }
    }

}