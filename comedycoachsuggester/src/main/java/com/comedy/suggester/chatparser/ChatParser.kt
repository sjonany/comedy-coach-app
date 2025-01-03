package com.comedy.suggester.chatparser

import android.view.accessibility.AccessibilityNodeInfo

interface ChatParser {
    /**
     * Given rootInActiveWindow, which is the snapshot from
     * [ChatWatcherAccessibilityService].onAccessibilityEvent, parse the chat messages
     */
    fun parseChatFromRootNode(rootInActiveWindow: AccessibilityNodeInfo): ChatMessages
}