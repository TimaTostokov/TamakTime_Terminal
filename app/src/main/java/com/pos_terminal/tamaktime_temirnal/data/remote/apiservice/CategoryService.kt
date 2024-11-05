package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.CATEGORY_ID_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CategoryService {

    @GET(CATEGORY_ID_END_POINT)
    suspend fun getCategories(
        @Header("Authorization") authHeader: String,
        @Path("canteenId") canteenId: Long
    ): Response<List<Category>>

}