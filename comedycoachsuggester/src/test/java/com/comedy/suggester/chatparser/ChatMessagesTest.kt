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
        chatList.addMessage("Alice", "Hello, everyone!", time)
        chatList.addMessage("Bob", "Hi Alice!", time)
        chatList.addMessage("Alice", "How are you doing, Bob?", time)
        chatList.addMessage("Charlie", "Good morning!", time)
        chatList.addMessage("Bob", "I'm doing well, thanks for asking!", time)
        chatList.obfuscateSenders()

        assertThat(chatList.toString()).isEqualTo(
            "[2024-12-01T01:00:01] Person 0: Hello, everyone!\n" +
                    "[2024-12-01T01:00:01] Person 1: Hi Alice!\n" +
                    "[2024-12-01T01:00:01] Person 0: How are you doing, Bob?\n" +
                    "[2024-12-01T01:00:01] Person 2: Good morning!\n" +
                    "[2024-12-01T01:00:01] Person 1: I'm doing well, thanks for asking!"
        )
    }
}