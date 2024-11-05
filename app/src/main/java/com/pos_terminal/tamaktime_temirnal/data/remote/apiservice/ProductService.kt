package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.PRODUCTS_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET(PRODUCTS_END_POINT)
    suspend fun getProductsByCanteenIdAndCategoryId(
        @Header("Authorization") authHeader: String,
        @Path("canteenId") canteenId: Long,
        @Query("category_id") categoryId: Long,
        @Query("per_page") perPage: Int,
    ): Response<ProductsResponse>

}