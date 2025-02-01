package com.comedy.suggester.generator

import com.comedy.suggester.chatparser.ChatMessage
import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class PromptStringsTest {

    @Test
    fun chatMesssagesToPrompt() {
        val chatList = ChatMessages()
        val time: LocalDateTime = LocalDateTime.of(2024, 12, 1, 1, 0, 1, 0)

        // Add test messages
        addMessage(chatList, "Alice", "Hello, everyone!", time)
        addMessage(chatList, "Bob", "Hi Alice!", time)

        val prompt = PromptStrings.suggestionGenerationPrompt(
            chatList, "make it punny",
            characterProfilesById = mapOf(
                "Alice" to CharacterProfile(
                    id = "AliceId", senseOfHumor = "AliceDescription", aliases = mapOf()
                )
            )
        )
        println(prompt)
        assertThat(prompt).isEqualTo(
            trimLines(
                """
You are an AI assistant specialized in generating humorous responses for a chat application. Your task is to create 5 funny replies that fit the context of an ongoing conversation, align with the characters' senses of humor, and incorporate a user-provided hint for the joke angle.

First, review the chat history:

<chat_history>
Alice: Hello, everyone!
Bob: Hi Alice!
</chat_history>

Take into account all these character's senses of humor:
<sense_of_humor>
<AliceId>
AliceDescription
</AliceId>
</sense_of_humor>


The user has provided a hint for the desired joke angle or theme:

<user_hint>
make it punny
</user_hint>

Your task is to generate 5 distinct funny responses that meet the following criteria:
1. Fit naturally within the context of the chat history
2. Match my sense of humor
3. Incorporate elements from the user's hint
4. Make the response short - at most one sentence and informal, like amongst close friends who are 20 year olds.

Only reply with the responses following this format (replace with your actual funny responses):
- [Response 1]
- [Response 2]
- [Response 3]
- [Response 4]
- [Response 5]
"""
            )
        )
    }

    fun addMessage(chatList: ChatMessages, sender: String, message: String, time: LocalDateTime) {
        chatList.addMessage(ChatMessage(sender, message, time))
    }
}