package com.comedy.suggester.chatparser

import com.comedy.suggester.ChatWatcherAccessibilityService.Companion.DISCORD_PACKAGE
import com.comedy.suggester.ChatWatcherAccessibilityService.Companion.WHATSAPP_PACKAGE

object ChatParserFactory {
    fun getChatParser(packageName: String): ChatParser {
        return when (packageName) {
            DISCORD_PACKAGE -> DiscordChatParser()
            WHATSAPP_PACKAGE -> WhatsAppChatParser()
            else -> throw IllegalArgumentException("Unknown package name: $packageName")
        }
    }
}