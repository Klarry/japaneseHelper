package com.japanesehelper.domain.model

/**
 * Which model answers the next message (Day 27).
 *
 * Two halves of the project, not a setting with a default that drifts.
 * [CLOUD] is everything the screen did before: the learning agent, the
 * document index, the mini chat - all of it through Gemini on the backend.
 * [LOCAL] is a model running on the machine the backend runs on, reached
 * through Ollama.
 *
 * Nothing switches between them on its own. If the local model is not
 * running, a local question fails and says so; it does not quietly become
 * a cloud question, because then the answer would no longer mean what the
 * switch says it means.
 */
enum class ModelProvider(
    /** What goes on a /rag/chat request (Day 28): the backend's own name
     * for this provider. The chips say Cloud and Local LLM because that is
     * what the person is choosing between; this is what it means to the
     * pipeline. */
    val wire: String
) {
    CLOUD("gemini"),
    LOCAL("ollama");

    val isLocal: Boolean get() = this == LOCAL
}

/**
 * One answer from the local model.
 *
 * [provider] comes from the backend rather than being assumed here: the
 * screen says "answered locally" only because the backend said so.
 */
data class LocalLlmAnswer(
    val response: String,
    val model: String,
    val provider: String,
    val seconds: Double = 0.0
)

/**
 * Whether the local model can be asked at all.
 *
 * Two separate facts, because they fail separately and the person can only
 * fix them separately: [available] is "the server answered", and
 * [modelInstalled] is "the model this backend would ask for is there".
 */
data class LocalLlmHealth(
    val available: Boolean,
    val model: String,
    val modelInstalled: Boolean = false,
    val models: List<String> = emptyList(),
    val error: String = ""
) {
    /** Ready to be asked: the server is up *and* it has the model. */
    val isReady: Boolean get() = available && modelInstalled
}
