package com.japanesehelper.data.remote

data class DescriptionConstraints(
    val format: String,
    val maxLength: String,
    val stopSequence: String
) {
    override fun toString(): String =
        "format=$format • max=$maxLength • stop=$stopSequence"
}

/**
 * A log-only mirror of the prompts the backend builds; nothing here is ever sent.
 * Keep in sync with the backend, or the demo log drifts from what Gemini receives.
 */
object DescriptionPrompts {

    const val PROVIDER = "Gemini"

    val CONSTRAINTS = DescriptionConstraints(
        format = "Markdown, heading + max 3 bullets",
        maxLength = "80 words",
        stopSequence = "<END>"
    )

    fun uncontrolled(meaning: String): String =
        "Explain the meaning \"$meaning\" of a Japanese vocabulary word to a JLPT learner."

    fun controlled(meaning: String): String =
        uncontrolled(meaning) +
            " Answer in ${CONSTRAINTS.format}." +
            " Use at most ${CONSTRAINTS.maxLength}." +
            " Finish with ${CONSTRAINTS.stopSequence} and write nothing after it."
}
