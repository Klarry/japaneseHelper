package com.japanesehelper.tools.extensions

inline fun <reified T : Enum<T>> String.toEnumOr(default: T): T {
    return try {
        enumValueOf<T>(this)
    } catch (_: IllegalArgumentException) {
        default
    }
}
