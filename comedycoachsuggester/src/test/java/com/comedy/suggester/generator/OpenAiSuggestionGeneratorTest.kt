package com.comedy.suggester.generator

import com.aallam.openai.client.OpenAI
import com.comedy.suggester.chatparser.ChatMessage
import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

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

    @Test
    fun chatMesssagesToPrompt() {
        val chatList = ChatMessages()
        val time: LocalDateTime = LocalDateTime.of(2024, 12, 1, 1, 0, 1, 0)

        // Add test messages
        addMessage(chatList, "Alice", "Hello, everyone!", time)
        addMessage(chatList, "Bob", "Hi Alice!", time)

        val prompt = generator.chatMessagesToPrompt(
            chatList, "make it punny",
            characterProfilesById = mapOf(
                "Alice" to CharacterProfile(
                    id = "AliceId", description = "AliceDescription", aliases = mapOf()
                )
            )
        )

        println(
            trimLines(
                """You will be given character descriptions between a friend and me, a chat history, and be
                asked to provide funny responses.
                
                Here are the character descriptions:
                [AliceId profile]
                AliceDescription
                
                [Chat history]
                Alice: Hello, everyone!
                Bob: Hi Alice!
    
                [Task]
                Let’s think step by step. Answer each of these questions in order
                Which concepts in the most recent message written by my friend am I likely to make a joke of given both our character profiles?
                What joke angles and tones might I want to use for these concepts?
                Also, incorporate the following: make it punny.
    
                Finally, given the joke angles and tones, please suggest 5 funny responses that I 
                am likely to write to my friend in the chat, with hyphen as bullet points and separated
                by newline (E.g. - Content1\n - Content2).
                Do NOT include the person name in your response.
        """
            )
        )
        assertThat(prompt).isEqualTo(
            trimLines(
                """You will be given character descriptions between a friend and me, a chat history, and be
                asked to provide funny responses.
                
                Here are the character descriptions:
                [AliceId profile]
                AliceDescription
                
                [Chat history]
                Alice: Hello, everyone!
                Bob: Hi Alice!
    
                [Task]
                Let’s think step by step. Answer each of these questions in order
                Which concepts in the most recent message written by my friend am I likely to make a joke of given both our character profiles?
                What joke angles and tones might I want to use for these concepts?
                Also, incorporate the following: make it punny.
    
                Finally, given the joke angles and tones, please suggest 5 funny responses that I 
                am likely to write to my friend in the chat, with hyphen as bullet points and separated
                by newline (E.g. - Content1\n - Content2).
                Do NOT include the person name in your response.
        """
            )
        )
    }

    fun addMessage(chatList: ChatMessages, sender: String, message: String, time: LocalDateTime) {
        chatList.addMessage(ChatMessage(sender, message, time))
    }
}