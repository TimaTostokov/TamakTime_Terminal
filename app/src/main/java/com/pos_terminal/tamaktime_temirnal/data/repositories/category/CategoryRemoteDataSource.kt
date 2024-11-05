package com.pos_terminal.tamaktime_temirnal.data.repositories.category

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.CategoryService
import javax.inject.Inject

class CategoryRemoteDataSource @Inject constructor(
    private val categoryService: CategoryService,
) : BaseDataSource() {

    suspend fun getCategories(header: String, canteenId: Long) =
        getResult { categoryService.getCategories(header, canteenId) }

}