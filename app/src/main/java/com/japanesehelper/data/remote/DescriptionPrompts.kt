package com.japanesehelper.data.remote

/**
 * The response constraints applied to the CONTROLLED generation.
 *
 * Mirrors the backend's constraint set so it can be shown in the demo log.
 */
data class DescriptionConstraints(
    val format: String,
    val maxLength: String,
    val stopSequence: String
) {
    /** Compact single-line rendering for Logcat. */
    override fun toString(): String =
        "format=$format • max=$maxLength • stop=$stopSequence"
}

/**
 * A local, log-only mirror of the prompts the backend sends to Gemini.
 *
 * The Android client posts only the vocabulary meaning to `POST /description`;
 * the backend builds the actual prompts. Nothing here is sent anywhere, and
 * nothing here changes the request — these values exist purely so the demo log
 * can show what the meaning is turned into downstream.
 *
 * **Keep in sync with the backend.** If the backend prompt or its constraints
 * change, update the templates below to match, or the log will drift from what
 * Gemini actually receives.
 */
object DescriptionPrompts {

    /** The provider the backend calls on our behalf. */
    const val PROVIDER = "Gemini"

    /** Constraints attached to the CONTROLLED generation only. */
    val CONSTRAINTS = DescriptionConstraints(
        format = "Markdown, heading + max 3 bullets",
        maxLength = "80 words",
        stopSequence = "<END>"
    )

    /** The prompt used for the unconstrained generation. */
    fun uncontrolled(meaning: String): String =
        "Explain the meaning \"$meaning\" of a Japanese vocabulary word to a JLPT learner."

    /** The prompt used for the format/length/termination-constrained generation. */
    fun controlled(meaning: String): String =
        uncontrolled(meaning) +
            " Answer in ${CONSTRAINTS.format}." +
            " Use at most ${CONSTRAINTS.maxLength}." +
            " Finish with ${CONSTRAINTS.stopSequence} and write nothing after it."
}
