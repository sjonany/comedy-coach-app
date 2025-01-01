package com.comedy.suggester.chatparser

import java.time.LocalDateTime

/**
 * All apps' chats get parsed to this class.
 */
class ChatMessages {
    private val messages: MutableList<ChatMessage> = mutableListOf()

    fun addMessage(sender: String, message: String, timestamp: LocalDateTime) {
        val chatMessage = ChatMessage(sender, message, timestamp)
        messages.add(chatMessage)
    }

    fun getMessage(index: Int): ChatMessage? {
        return if (index < messages.size) messages[index] else null
    }

    fun getSize(): Int {
        return messages.size
    }

    fun sortMessagesByTimestamp() {
        messages.sortBy { it.timestamp }
    }

    // Obfuscate sender names with pseudonyms
    fun obfuscateSenders() {
        val senderToPseudonym = mutableMapOf<String, String>()

        messages.replaceAll { message ->
            val pseudonym = senderToPseudonym.getOrPut(message.sender) {
                "Person ${senderToPseudonym.size}"
            }
            message.copy(sender = pseudonym)
        }
    }

    override fun toString(): String {
        return messages.joinToString(separator = "\n") { message ->
            "[${message.timestamp}] ${message.sender}: ${message.message}"
        }
    }
}

/**
 * A single chat message
 */
data class ChatMessage(
    val sender: String,
    val message: String,
    val timestamp: LocalDateTime
)
