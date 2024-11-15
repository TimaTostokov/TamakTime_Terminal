package com.pos_terminal.tamaktime_temirnal.data.repositories.product

import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.ProductService
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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