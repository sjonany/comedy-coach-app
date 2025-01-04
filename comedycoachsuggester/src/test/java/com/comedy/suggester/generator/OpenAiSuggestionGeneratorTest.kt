package com.comedy.suggester.generator

import com.aallam.openai.client.OpenAI
import com.comedy.suggester.chatparser.ChatMessage
import com.comedy.suggester.chatparser.ChatMessages
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class OpenAiSuggestionGeneratorTest {
    val generator = OpenAiSuggestionGenerator(OpenAI("fake key"))

    @Test
    fun parseLlmResponse() {
        val suggestions = generator.parseLlmResponse(
            "Here is the response:\n" +
                    "- First suggestion\n" +
                    "- Second suggestion\n" +
                    "Have a good day!"
        )
        assertThat(suggestions).containsExactly("First suggestion", "Second suggestion")
    }

    @Test
    fun chatMesssagesToPrompt() {
        val chatList = ChatMessages()
        val time: LocalDateTime = LocalDateTime.of(2024, 12, 1, 1, 0, 1, 0)

        // Add test messages
        addMessage(chatList, "Alice", "Hello, everyone!", time)
        addMessage(chatList, "Bob", "Hi Alice!", time)

        val prompt = generator.chatMessagesToPrompt(chatList, "make it punny")
        assertThat(prompt).isEqualTo(
            "Please suggest 5 funny responses to this chat history, " +
                    "with hyphen as bullet points and separated by newline (E.g. - Content1\n - Content2). " +
                    "Also, make it punny.\n" +
                    "Do NOT include the person name in your response.\n" +
                    "Chat history:\n" +
                    "Person 0: Hello, everyone!\n" +
                    "Person 1: Hi Alice!"
        )
    }

    fun addMessage(chatList: ChatMessages, sender: String, message: String, time: LocalDateTime) {
        chatList.addMessage(ChatMessage(sender, message, time))
    }
}