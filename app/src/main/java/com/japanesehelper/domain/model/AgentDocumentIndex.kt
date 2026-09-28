package com.japanesehelper.domain.model

/**
 * The local document index the backend built, as a few numbers.
 *
 * Reported, not built. Loading documents, reading PDFs, chunking, embedding
 * and FAISS all happen on the backend, in a command that is run there; the
 * device asks one endpoint how big the result is and draws four lines of it.
 * ``found`` is false when no index has been built yet - an answer, not an
 * error.
 */
data class AgentDocumentIndex(
    val found: Boolean = false,
    val documents: Int = 0,
    val totalCharacters: Int = 0,
    val fixedChunks: Int = 0,
    val structuralChunks: Int = 0,
    val embeddingModel: String = "",
    val embeddingDimension: Int = 0,
    val builtAt: String = ""
)
