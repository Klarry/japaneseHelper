package com.japanesehelper.data.remote

/**
 * The one prompt Day 5 uses to compare Gemini models - identical for every
 * model in [com.japanesehelper.domain.model.COMPARISON_MODELS]. Unlike
 * [DescriptionPrompts], this text really is sent to the backend as-is: the
 * `{KANJI}` placeholder is substituted server-side (see `_build_prompt` in
 * the backend's model_comparison_service).
 */
const val MODEL_COMPARISON_PROMPT =
    "Compose one natural Japanese sentence using the kanji {KANJI}.\n" +
        "Use common vocabulary and a language level around N4-N3.\n" +
        "Add a Russian translation."
