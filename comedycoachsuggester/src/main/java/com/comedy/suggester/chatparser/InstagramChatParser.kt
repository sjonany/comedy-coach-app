package com.comedy.suggester.chatparser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Parses chat from an instagram chat window
 * TODO: Handle group chats
 * WARNING: This only handles 1-1 chat.
 * In a group chat, the layout will be different because each person's name is also listed.
 * Not handled yet.
 */
class InstagramChatParser : ChatParser {
    companion object {
        private const val LOG_TAG = "InstagramChatParser"
    }

    override fun parseChatFromRootNode(rootInActiveWindow: AccessibilityNodeInfo): ChatMessages {
        Log.d(LOG_TAG, "parseChatFromRootNode")
        // logNodeTree(rootInActiveWindow)

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
            if (chatBubble.className != "android.widget.FrameLayout") {
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
        for (i in 0 until chatBubble.childCount) {
            val child = chatBubble.getChild(i)
            if (child.className != "android.widget.TextView") {
                continue
            }
            if (child.text != null && child.contentDescription != null &&
                child.text.toString() == child.contentDescription
            ) {
                // TODO: Parse people names
                return ChatMessage("friend", child.text.toString(), null)
            }
        }
        return null
    }

    /*
Some observations:"
Go through the first androidx.recyclerview.widget.RecyclerView
For each android.widget.FrameLayout, find the first android.widget.TextView where both the text and
contentDesc are non-null and they're both equal.
Hard to determine the speaker. Let's just hardcode as "friend"

Here's an example exchange:
 Class: android.widget.FrameLayout, Text: null, ContentDesc: null
     Class: android.view.ViewGroup, Text: null, ContentDesc: null
       Class: android.widget.FrameLayout, Text: null, ContentDesc: null
         Class: android.widget.ImageView, Text: null, ContentDesc: Back
         Class: android.widget.Button, Text: null, ContentDesc: null
           Class: android.widget.Button, Text: Friend's name, ContentDesc: Friend's name
         Class: android.widget.ImageView, Text: null, ContentDesc: Instagram audio call
         Class: android.widget.ImageView, Text: null, ContentDesc: Instagram Video Call
         Class: android.widget.FrameLayout, Text: null, ContentDesc: null
           Class: android.widget.TextView, Text: Make this chat your own by creating fun nicknames for each other., ContentDesc: Make this chat your own by creating fun nicknames for each other.
           Class: android.widget.ImageView, Text: null, ContentDesc: Close
           Class: android.widget.Button, Text: Try it, ContentDesc: null
         Class: androidx.recyclerview.widget.RecyclerView, Text: null, ContentDesc: null
           Class: android.widget.FrameLayout, Text: null, ContentDesc: null
             Class: android.widget.FrameLayout, Text: null, ContentDesc: null
               Class: android.widget.ImageView, Text: null, ContentDesc: Profile picture
               Class: android.widget.TextView, Text: image caption, ContentDesc: null
             Class: android.widget.ImageView, Text: null, ContentDesc: Forward message
             Class: android.widget.ImageView, Text: null, ContentDesc: Save to collection
           Class: android.widget.FrameLayout, Text: null, ContentDesc: null
             Class: android.widget.ImageView, Text: null, ContentDesc: Profile picture
             Class: android.widget.TextView, Text: New aspirations..., ContentDesc: New aspirations...
           Class: android.widget.FrameLayout, Text: null, ContentDesc: null
             Class: android.widget.TextView, Text: way too clean, ContentDesc: way too clean
           Class: android.widget.LinearLayout, Text: null, ContentDesc: null
             Class: android.widget.TextView, Text: Seen 6h ago, ContentDesc: null
         Class: androidx.recyclerview.widget.RecyclerView, Text: null, ContentDesc: null
         Class: android.widget.FrameLayout, Text: null, ContentDesc: null
           Class: android.widget.LinearLayout, Text: null, ContentDesc: null
             Class: android.widget.ImageView, Text: null, ContentDesc: Sticker
             Class: android.widget.EditText, Text: Message…, ContentDesc: null
     */
}
