package com.comedy.suggester.chatparser

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class ChatMessagesTest {
    @Test
    fun obfuscateSenders() {
        val chatList = ChatMessages()
        val time: LocalDateTime = LocalDateTime.of(2024, 12, 1, 1, 0, 1, 0)

        // Add test messages
        addMessage(chatList, "Alice", "Hello, everyone!", time)
        addMessage(chatList, "Bob", "Hi Alice!", time)
        addMessage(chatList, "Alice", "How are you doing, Bob?", time)
        addMessage(chatList, "Charlie", "Good morning!", time)
        addMessage(chatList, "Bob", "I'm doing well, thanks for asking!", time)
        chatList.obfuscateSenders()

        assertThat(chatList.toString()).isEqualTo(
            "[2024-12-01T01:00:01] Person 0: Hello, everyone!\n" +
                    "[2024-12-01T01:00:01] Person 1: Hi Alice!\n" +
                    "[2024-12-01T01:00:01] Person 0: How are you doing, Bob?\n" +
                    "[2024-12-01T01:00:01] Person 2: Good morning!\n" +
                    "[2024-12-01T01:00:01] Person 1: I'm doing well, thanks for asking!"
        )
    }

    fun addMessage(chatList: ChatMessages, sender: String, message: String, time: LocalDateTime) {
        chatList.addMessage(ChatMessage(sender, message, time))
    }
}