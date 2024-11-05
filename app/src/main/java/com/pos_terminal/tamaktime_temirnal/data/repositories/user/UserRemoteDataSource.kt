package com.pos_terminal.tamaktime_temirnal.data.repositories.user

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.UserService
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userService: UserService,
) : BaseDataSource() {

    suspend fun me(header: String) = getResult { userService.me(header) }

}