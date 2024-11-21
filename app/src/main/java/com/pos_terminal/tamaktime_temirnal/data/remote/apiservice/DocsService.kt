package com.pos_terminal.tamaktime_temirnal.data.remote.apiservice

import com.pos_terminal.tamaktime_temirnal.common.Constants.PUT_DOCUMENT_END_POINT
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentRequestBody
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentResponse
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path


interface DocsService {

    @PUT(PUT_DOCUMENT_END_POINT)
    suspend fun updateDocument(
        @Header("Authorization") authHeader: String,
        @Path("canteen_id") canteenId: String,
        @Path("document_id") documentId: String,
        @Body requestBody: DocumentRequestBody
    ): Response<DocumentResponse>
}
