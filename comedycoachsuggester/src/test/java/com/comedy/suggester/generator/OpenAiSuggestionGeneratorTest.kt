package com.comedy.suggester.generator

import com.aallam.openai.client.OpenAI
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OpenAiSuggestionGeneratorTest {
    val generator = OpenAiSuggestionGenerator(OpenAI("fake key"))

    @Test
    fun parseLlmResponse() {
        val suggestions = generator.parseLlmResponse(
            """
                Here's a random list
                - item 1
                - item 2
                
                Here is the response:
                - First suggestion
                - Second suggestion
                Have a good day!
                
            """.trimIndent()
        )
        assertThat(suggestions).containsExactly("First suggestion", "Second suggestion")
    }
}