package com.pos_terminal.tamaktime_temirnal.data.repositories.product

import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
) {

    suspend fun getProductsByCanteenIdAndCategoryId(
        header: String,
        canteenId: Long,
        categoryId: Long,
    ) =
        remoteDataSource.getProductsByCanteenIdAndCategoryId(header, canteenId, categoryId)

}