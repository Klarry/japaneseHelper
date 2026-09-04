package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.ModelComparisonResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The backend replies with snake_case keys (response_time_ms, input_tokens,
 * output_tokens); this locks in the @SerializedName mapping against real Gson
 * parsing rather than just copying fields between already-typed objects.
 */
class ModelComparisonMapperTest {

    private val gson = Gson()

    @Test
    fun `snake_case token and timing fields deserialize and map correctly`() {
        val json = """
            {
              "prompt": "resolved prompt",
              "results": [
                {
                  "model": "gemini-3.5-flash",
                  "text": "こんにちは",
                  "response_time_ms": 842,
                  "input_tokens": 12,
                  "output_tokens": 34,
                  "error": null
                },
                {
                  "model": "gemini-3.1-pro-preview",
                  "text": null,
                  "response_time_ms": 15,
                  "input_tokens": null,
                  "output_tokens": null,
                  "error": "quota exceeded"
                }
              ]
            }
        """.trimIndent()

        val dto = gson.fromJson(json, ModelComparisonResponseDto::class.java)
        val domain = dto.toDomain()

        assertEquals("resolved prompt", domain.prompt)
        assertEquals(2, domain.results.size)

        val success = domain.results[0]
        assertEquals("gemini-3.5-flash", success.model)
        assertEquals("こんにちは", success.text)
        assertEquals(842, success.responseTimeMs)
        assertEquals(12, success.inputTokens)
        assertEquals(34, success.outputTokens)
        assertNull(success.error)

        val failure = domain.results[1]
        assertEquals("gemini-3.1-pro-preview", failure.model)
        assertNull(failure.text)
        assertEquals(15, failure.responseTimeMs)
        assertNull(failure.inputTokens)
        assertNull(failure.outputTokens)
        assertEquals("quota exceeded", failure.error)
    }
}
