package com.pos_terminal.tamaktime_temirnal.data.repositories.category

import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val remote: CategoryRemoteDataSource,
) {

    suspend fun getCategories(header: String, canteenId: Long) =
        remote.getCategories(header, canteenId)

}