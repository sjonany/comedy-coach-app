package com.comedy.suggester.chatparser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Parser for discord chat.
 * The input is an accessibility root node from an event triggered in a discord chat.
 * TODO: Handle group chat. Right now this only handles 1-1
 */
class DiscordChatParser : ChatParser {
    companion object {
        private const val LOG_TAG = "DiscordChatParser"
    }

    /**
     * Given rootInActiveWindow, which is the snapshot from
     * [ChatWatcherAccessibilityService].onAccessibilityEvent, parse the chat messages
     */
    override fun parseChatFromRootNode(rootInActiveWindow: AccessibilityNodeInfo): ChatMessages {
        val rawTexts: List<String> = getRawTextsFromNode(rootInActiveWindow)
        return parseRawTexts(rawTexts, LocalDateTime.now())
    }


    // Raw text content in discord has this as delimiter
    private val RAW_TEXT_DELIMITER = ", "

    /**
     * See [getRawTextsFromNode] for examples of raw texts
     *
     * -- Structure
     * Here's the structure of the text.
     * - Person name (OR alias)
     *   - The aliases are used when someone is sending a message consecutively. I think one is the discord
     *     display name, and the other is the discord id?
     * - comma and space
     * - (Optional) a time marker like "Today at 7:52 AM" or "12/08/2024 2:14 PM"
     *   - Time markers all follow this regex:
     *     Today at xx:xx am/pm
     *     Yesterday at xx:xx am/pm
     *     12/25/2024 4:27 PM
     *   - Alias. If something has a time marker, then the next few messages without a time marker is the
     *     same person and we resolve the alias.
     *     Note that this doesn't solve Ex 3 though, if we don't know a == a_alias. I'm ok w/ this edge
     *     case, we will just fail to resolve the identity of at most one at the top.
     * - comma and space
     * - (Optional) Replying to <name> -- This is when you swipe right on a message
     * - comma and space
     * - Actual message
     */
    internal fun parseRawTexts(rawTexts: List<String>, today: LocalDateTime): ChatMessages {
        // non-timemarked name to timemarked name
        val nameMap = hashMapOf<String, String>()
        var mostRecentTimeMarkedName: String? = null
        var mostRecentTimeSeen: LocalDateTime? = null
        val parsedMessages = ChatMessages()
        for (rawText in rawTexts) {
            val tokens: List<String> = rawText.split(RAW_TEXT_DELIMITER)
            var curTokenIndex = 0
            if (curTokenIndex == tokens.size) {
                Log.d(LOG_TAG, "Unexpected raw text: $rawText")
                continue
            }

            val name: String = tokens[curTokenIndex]
            curTokenIndex++

            // Check if this is a timestamp
            if (curTokenIndex == tokens.size) {
                Log.d(LOG_TAG, "Unexpected raw text: $rawText")
                continue
            }
            val timeMarker: LocalDateTime? = parseTimeMarker(tokens[curTokenIndex], today)

            if (timeMarker == null) {
                if (mostRecentTimeMarkedName != null) {
                    nameMap[name] = mostRecentTimeMarkedName
                }
            } else {
                curTokenIndex++
                mostRecentTimeSeen = timeMarker
                mostRecentTimeMarkedName = name
            }

            if (curTokenIndex == tokens.size) {
                Log.d(LOG_TAG, "Unexpected raw text: $rawText")
                continue
            }

            // Skip "Replying to"
            if (tokens[curTokenIndex].startsWith("Replying to ") &&
                // And there's still content to parse
                curTokenIndex + 1 < tokens.size
            ) {
                curTokenIndex++
            }

            if (curTokenIndex == tokens.size) {
                Log.d(LOG_TAG, "Unexpected raw text: $rawText")
                continue
            }

            // The remainder is just the message, concatenate all of them
            val message = tokens.subList(curTokenIndex, tokens.size).joinToString(", ")
            if (message.isEmpty()) {
                continue
            }
            parsedMessages.addMessage(ChatMessage(name, message, mostRecentTimeSeen))
        }

        // Convert aliases to the time-marked name
        val nameResolvedMessages = parsedMessages.getMessages().map { message ->
            message.copy(sender = nameMap[message.sender] ?: message.sender)
        }
        return ChatMessages(nameResolvedMessages)
    }


    private val TODAY_PREFIX = "Today at"
    private val YESTERDAY_PREFIX = "Yesterday at"
    private val LOCAL_DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a", Locale.ENGLISH)
    private val DISCORD_TIME_FORMAT =
        DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.ENGLISH)

    /**
     * Time markers all follow this regex:
     *    Today at xx:xx am/pm
     *    Yesterday at xx:xx am/pm
     *    12/25/2024 4:27 PM
     */
    private fun parseTimeMarker(timeMarker: String, today: LocalDateTime): LocalDateTime? {
        val yesterday = today.minusDays(1)

        return try {
            when {
                timeMarker.startsWith(TODAY_PREFIX) -> {
                    val timePart = timeMarker.substring(TODAY_PREFIX.length).trim()
                    LocalDateTime.parse(
                        today.toLocalDate().toString() + " " + timePart,
                        LOCAL_DATE_TIME_FORMAT
                    )
                }

                timeMarker.startsWith(YESTERDAY_PREFIX) -> {
                    val timePart = timeMarker.substring(YESTERDAY_PREFIX.length).trim()
                    LocalDateTime.parse(
                        yesterday.toLocalDate().toString() + " " + timePart,
                        LOCAL_DATE_TIME_FORMAT
                    )
                }

                else -> {
                    LocalDateTime.parse(timeMarker, DISCORD_TIME_FORMAT)
                }
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * Get the chat raw texts from the root node.
     *
     *     The chat structure looks like this:
     *     parent = androidx.recyclerview.widget.RecyclerView = rootInActiveWindow.getChild(0).getChild(0).getChild(5)
     *     parent > android.widget.RelativeLayout.Text contains all the text, and looks like this:
     *
     * -- Examples
     * Ex 1
     * 0 = "a, Message content bla3"
     * 1 = "a, Message content bla3"
     * 2 = "b, 12/02/2024 11:56 PM, Message content bla3"
     * 3 = "b_alias, Message content bla3"
     * 4 = "a, 12/03/2024 8:49 AM, Message content bla3"
     * 5 = "a_alias, Message content bla3"
     *
     * Ex 2
     * 0 = "a, Thank you so much for coming!!"
     * 1 = "a, Message content bla3"
     * 2 = "a, Message content bla3"
     * 3 = "b, Today at 7:52 AM, Replying to a, No no this was actually v funny and interesting"
     * 4 = "b, Message content bla3"
     * 5 = "b, Message content bla3"
     * 6 = "b, Message content bla3"
     *
     * Ex 3
     * 0 = "a_alias, Message content bla3"
     * 1 = "b, 12/08/2024 2:14 PM, Message content bla3"
     * 2 = "c, 12/08/2024 4:02 PM, Message content bla3"
     * 3 = "a, 12/25/2024 10:06 AM, Message content bla3"
     * 4 = "b, 12/25/2024 4:27 PM, Message content bla3"
     */
    private fun getRawTextsFromNode(rootInActiveWindow: AccessibilityNodeInfo): List<String> {
        val firstRecyclerView = findFirstNodeWithClassName(
            rootInActiveWindow,
            "androidx.recyclerview.widget.RecyclerView"
        )
        if (firstRecyclerView == null) {
            Log.d(LOG_TAG, "can't find first recycler view")
            return listOf()
        }

        val rawTexts: MutableList<String> = mutableListOf()

        for (i in 0 until firstRecyclerView.childCount) {
            val child = firstRecyclerView.getChild(i)
            if (child.className != "android.widget.RelativeLayout" || child.text == null) {
                continue
            }
            rawTexts.add(child.text.toString())
        }
        return rawTexts.toList()
    }
}