package com.pos_terminal.tamaktime_temirnal.data.repositories.product

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.ProductService
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val productService: ProductService,
) : BaseDataSource() {
    suspend fun getProductsByCanteenIdAndCategoryId(
        header: String,
        canteenId: Long,
        categoryId: Long,
    ) = getResult {
        productService.getProductsByCanteenIdAndCategoryId(
            header,
            canteenId,
            categoryId,
            50
        )
    }

}