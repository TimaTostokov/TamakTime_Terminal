package com.pos_terminal.tamaktime_temirnal.data.repositories.documents

import com.pos_terminal.tamaktime_temirnal.common.BaseDataSource
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.DocsService
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentRequestBody
import javax.inject.Inject

class DocumentRemoteDataSourse @Inject constructor(
    private val documentService: DocsService,
) : BaseDataSource() {

    suspend fun updateDocument(
        authHeader: String,
        canteenId: String,
        documentId: String,
        documentRequestBody: DocumentRequestBody
    ) = getResult {
        documentService.updateDocument(authHeader, canteenId, documentId, documentRequestBody)
    }
}
