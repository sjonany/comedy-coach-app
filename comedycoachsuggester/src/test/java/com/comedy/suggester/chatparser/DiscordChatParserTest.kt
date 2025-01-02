package com.comedy.suggester.chatparser

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class DiscordChatParserTest {
    val TODAY = LocalDateTime.of(2024, 12, 1, 1, 0, 1, 0)
    val discordChatParser = DiscordChatParser()

    /*
    All messages are timestamped. Also test all the time formats
     */
    @Test
    fun parseRawTexts_allTimes() {
        val messages = listOf(
            "p1, 12/08/2024 2:14 PM, First message",
            "p1, Yesterday at 12:15 PM, Second message",
            "p2, Today at 3:13 PM, Third message"
        )
        val result = discordChatParser.parseRawTexts(
            messages,
            TODAY
        )
        assertThat(result.toString()).isEqualTo(
            "[2024-12-08T14:14] p1: First message\n" +
                    "[2024-11-30T12:15] p1: Second message\n" +
                    "[2024-12-01T15:13] p2: Third message"
        )
    }

    /* No time mark at all */
    @Test
    fun parseRawTexts_noTime() {
        val messages = listOf(
            "p1, First message",
            "p1, Second message",
            "p2, Third message"
        )
        val result = discordChatParser.parseRawTexts(
            messages,
            TODAY
        )
        assertThat(result.toString()).isEqualTo(
            "[null] p1: First message\n" +
                    "[null] p1: Second message\n" +
                    "[null] p2: Third message"
        )
    }

    /* Aliasing works, even for the guy at the top */
    @Test
    fun parseRawTexts_aliasing() {
        val messages = listOf(
            "p1_alias, First message",
            "p1_alias, Second message",
            "p2, Today at 3:13 PM, Third message",
            "p2_alias, Message 4",
            "p1, Today at 3:14 PM, M5",
            "p1_alias, M6",
        )
        val result = discordChatParser.parseRawTexts(
            messages,
            TODAY
        )
        assertThat(result.toString()).isEqualTo(
            "[null] p1: First message\n" +
                    "[null] p1: Second message\n" +
                    "[2024-12-01T15:13] p2: Third message\n" +
                    "[2024-12-01T15:13] p2: Message 4\n" +
                    "[2024-12-01T15:14] p1: M5\n" +
                    "[2024-12-01T15:14] p1: M6"
        )
    }

    /* Replying to skipped */
    @Test
    fun parseRawTexts_replySkipped() {
        val messages = listOf(
            "p1, 12/08/2024 2:14 PM, First message",
            "p1, Yesterday at 12:15 PM, Replying to a, Second message",
            "p2, Today at 3:13 PM, Replying to b is fun"
        )
        val result = discordChatParser.parseRawTexts(
            messages,
            TODAY
        )
        assertThat(result.toString()).isEqualTo(
            "[2024-12-08T14:14] p1: First message\n" +
                    "[2024-11-30T12:15] p1: Second message\n" +
                    "[2024-12-01T15:13] p2: Replying to b is fun"
        )
    }
}