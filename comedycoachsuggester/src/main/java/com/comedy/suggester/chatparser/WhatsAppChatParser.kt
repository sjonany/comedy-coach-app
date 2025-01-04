package com.comedy.suggester.chatparser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Parses chat from a whatsapp chat window
 * TODO: Handle group chats
 * WARNING: This only handles 1-1 chat.
 * In a group chat, the layout will be different because each person's name is also listed.
 * Not handled yet.
 */
class WhatsAppChatParser : ChatParser {
    companion object {
        private const val LOG_TAG = "WhatsAppChatParser"
    }

    override fun parseChatFromRootNode(rootInActiveWindow: AccessibilityNodeInfo): ChatMessages {
        Log.d(LOG_TAG, "parseChatFromRootNode")

        val chatParent = findFirstNodeWithClassName(
            rootInActiveWindow,
            "android.widget.ListView"
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

    // A chat bubble is one message. It contains info like delivery time, whether it's read / not
    // and the message
    fun parseChatBubble(chatBubble: AccessibilityNodeInfo): ChatMessage? {
        // We only handle 1-1 for now.
        // If sent by me, there's an image view at the end that says read/delivered
        val sender =
            if (chatBubble.getChild(chatBubble.childCount - 1).className == "android.widget.ImageView")
                "me" else "friend"

        val textViews = mutableListOf<AccessibilityNodeInfo>()
        for (i in 0 until chatBubble.childCount) {
            val child = chatBubble.getChild(i)
            if (child.className == "android.widget.TextView") {
                textViews.add(child)
            }
        }
        when (textViews.size) {
            0 -> return null
            1 -> return ChatMessage(sender, textViews[0].text.toString(), null)
            // Last text is usually the time. And sometimes the first text is as a header like "Today"
            // but is clumped into this view group
            else -> return ChatMessage(sender, textViews[textViews.size - 2].text.toString(), null)
        }
    }

    /*
Some observations:"

- Get the first ListView, there seems to be only 1
- Collect the ViewGroups
- For each ViewGroup, if ends with an ImageView, then it's yours, else it's your partner's
- Won't populate the time - it's not being used anyway.
- The actual message is the second to last text view, because the last text view is always time

Here's an example exchange:
ListView
- ViewGroup (mine)
  - TextView: "Message 1"
  - TextView: "4:06 PM"
  - ImageView: Read
- ViewGroup (friend)
  - TextView: "Today"
  - TextView: "Message 2"
  - TextView: "4:03 AM"
- ViewGroup (friend)
  - TextView: "Message 3"
  - TextView: "4:03 AM"
- ViewGroup (mine)
  - TextView: "Message 4"
  - TextView: "4:03 AM"
  - ImageView: Delivered
- ViewGroup (mine)
  - TextView: "Message 5"
  - TextView: "4:03 AM"
  - ImageView: Delivered
 - ViewGroup (mine, but I swiped right to reply)
   - FrameLayout - contains the message being replied to
   - TextView: "Message 6"
   - TextView: "4:03 AM"
   - ImageView: Delivered

     */
}
