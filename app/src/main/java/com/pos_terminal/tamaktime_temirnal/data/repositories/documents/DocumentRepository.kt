package com.pos_terminal.tamaktime_temirnal.data.repositories.documents

import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentRequestBody
import javax.inject.Inject

class DocumentRepository @Inject constructor(
    private val remote: DocumentRemoteDataSourse
) {
    suspend fun updateDocument(
        authHeader: String,
        canteenId: String,
        documentId: String,
        documentRequestBody: DocumentRequestBody
    ) = remote.updateDocument(authHeader, canteenId, documentId, documentRequestBody)
}
