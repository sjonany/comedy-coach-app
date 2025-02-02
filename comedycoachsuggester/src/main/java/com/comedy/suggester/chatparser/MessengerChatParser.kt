package com.comedy.suggester.chatparser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Parses chat from a messenger chat window.
 * This seems to handle both 1-1 and group chats
 */
class MessengerChatParser : ChatParser {
    companion object {
        private const val LOG_TAG = "MessengerChatParser"
    }

    override fun parseChatFromRootNode(rootInActiveWindow: AccessibilityNodeInfo): ChatMessages {
        Log.d(LOG_TAG, "parseChatFromRootNode")
        //logNodeTree(rootInActiveWindow)

        val chatParent = findFirstNodeWithClassName(
            rootInActiveWindow,
            "androidx.recyclerview.widget.RecyclerView"
        )

        if (chatParent == null) {
            Log.d(LOG_TAG, "can't find chatParent")
            return ChatMessages()
        }

        val result = ChatMessages()

        for (i in 0 until chatParent.childCount) {
            val chatBubble = chatParent.getChild(i)!!
            if (chatBubble.className != "android.view.ViewGroup") {
                continue
            }
            val parsedChatBubble = parseChatBubble(chatBubble)
            if (parsedChatBubble != null) {
                result.addMessage(
                    ChatMessage(parsedChatBubble.sender, parsedChatBubble.message, null)
                )
            }
        }
        return result
    }

    // A chat bubble is one message. It might contain images
    fun parseChatBubble(chatBubble: AccessibilityNodeInfo): ChatMessage? {
        val leaves = findMaxDepthLeaves(chatBubble)
        var bestText: String? = null

        for (leaf in leaves) {
            if (leaf.className != "android.view.ViewGroup") continue
            val content = leaf.contentDescription
            if (content == null || content == "Profile Picture" || content == "Add custom reaction"
                || content == "one reaction"
            ) {
                continue
            }
            if (bestText == null || bestText.length < content.length) {
                bestText = content.toString()
            }
        }
        if (bestText == null) return null
        // The text is of the format: <person name>, <actual message>
        val separatorIndex = bestText.indexOf(',')
        if (separatorIndex < 0) {
            return ChatMessage("friend", bestText, null)
        }
        val personName = bestText.substring(0, separatorIndex)
        val message = bestText.substring(separatorIndex + 2)
        return ChatMessage(personName, message, null)
    }
}
