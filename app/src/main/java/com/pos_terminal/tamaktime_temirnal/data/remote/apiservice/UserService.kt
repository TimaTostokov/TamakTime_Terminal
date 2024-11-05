package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.USERS_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.user.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface UserService {

    @Headers("Content-Type: application/json")
    @GET(USERS_END_POINT)
    suspend fun me(
        @Header("Authorization")
        authHeader: String,
    ): Response<User>

}