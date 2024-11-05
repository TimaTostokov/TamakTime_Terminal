package com.pos_terminal.tamaktime_temirnal.data.repositories.user

import com.pos_terminal.tamaktime_temirnal.common.LanguagePreference
import com.pos_terminal.tamaktime_temirnal.data.remote.local.UserStoreImpl
import com.pos_terminal.tamaktime_temirnal.data.remote.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val local: UserStoreImpl,
    private val remote: UserRemoteDataSource,
    private val languagePreference: LanguagePreference,
) {

    fun saveSelectedLanguage(languageCode: String) {
        languagePreference.saveLanguage(languageCode)
    }

    fun getSavedLanguage(): String {
        return languagePreference.getLanguage ?: "tr"
    }

    suspend fun logOut() = local.clear()

    suspend fun me(credentials: String) = remote.me("$credentials}")

    suspend fun showMe() {
        Timber.e(local.flowCredentials().collect().toString())
    }

    fun flowHasCredentials() = local.flowHasCredentials()
    fun flowCredentials() = local.flowCredentials()
    fun flowCanteenId(): Flow<Long?> = local.flowCanteenId()

    suspend fun getCredentials() = local.getCredentials()
    suspend fun getCanteenId() = local.getCanteenId()
    suspend fun getSchoolId() = local.getSchoolId()
    suspend fun getStudentId() = local.getStudentId()

    suspend fun saveUser(user: User) = local.saveUser(user)

    suspend fun clear() = local.clear()

}