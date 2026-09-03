package com.japanesehelper.presentation.navigation

import android.net.Uri

private const val KANJI_ARG_KEY = "kanji"
private const val WORD_ARG_KEY = "word"
private const val MEANING_ARG_KEY = "meaning"

sealed class Screens(val route: String) {
    data object Home : Screens("home")
    data object Details : Screens("details")

    data object KanjiWordSet : Screens("kanji_word_set/{$KANJI_ARG_KEY}") {
        const val ARG_KANJI = KANJI_ARG_KEY

        /** Builds a concrete, navigable route for the given dynamic kanji/word. */
        fun createRoute(kanji: String): String = "kanji_word_set/${Uri.encode(kanji)}"
    }

    data object AiExplanation : Screens("ai_explanation/{$WORD_ARG_KEY}/{$MEANING_ARG_KEY}") {
        const val ARG_WORD = WORD_ARG_KEY
        const val ARG_MEANING = MEANING_ARG_KEY

        /** Builds a concrete, navigable route for the given dynamic word/meaning. */
        fun createRoute(word: String, meaning: String): String =
            "ai_explanation/${Uri.encode(word)}/${Uri.encode(meaning)}"
    }
}
