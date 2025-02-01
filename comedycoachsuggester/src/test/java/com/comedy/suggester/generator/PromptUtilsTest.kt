package com.comedy.suggester.generator

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PromptUtilsTest {

    @Test
    fun parseLlmResponse() {
        val suggestions = parseLlmResponse(
            """
                Here's a random list
                - item 1
                - item 2
                
                Here is the response:
                - First suggestion
                
                - "Second suggestion"
                Have a good day!
                
            """.trimIndent()
        )
        assertThat(suggestions).containsExactly("First suggestion", "Second suggestion")
    }
}