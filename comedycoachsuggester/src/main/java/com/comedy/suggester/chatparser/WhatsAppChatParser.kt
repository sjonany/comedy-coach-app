package com.comedy.suggester.chatparser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class WhatsAppChatParser : ChatParser {
    companion object {
        private const val LOG_TAG = "WhatsAppChatParser"
    }

    override fun parseChatFromRootNode(rootInActiveWindow: AccessibilityNodeInfo): ChatMessages {
        Log.d(LOG_TAG, "parseChatFromRootNode")
        logNodeTree(rootInActiveWindow)
        return ChatMessages()
    }
}
