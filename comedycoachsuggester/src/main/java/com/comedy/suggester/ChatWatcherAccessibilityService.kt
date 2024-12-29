package com.comedy.suggester

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * This service watches for chat apps, and has a button that will trigger
 * ResponseSuggestionInputMethodService
 */
class ChatWatcherAccessibilityService : AccessibilityService() {
    private var suggestionFloatingWidgetManager: SuggestionFloatingWidgetManager? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        // Need overlay permission first. The settings activity handles all these
        if (!Settings.canDrawOverlays(this)) {
            Log.d(
                "ChatWatcherAccessibilityService",
                "can't draw overlays, starting settings activity"
            )
            val intent = Intent(this, ComedyCoachIMESettingsActivity::class.java)
            startActivity(intent)
            return
        }

        // Check if Discord is in the foreground
        Log.d("ChatWatcherAccessibilityService", "got event: $event")
        val packageName = event.packageName?.toString()
        if (packageName == "com.discord") {
            // Look for typing in a text field
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED &&
                event.className == "android.widget.EditText" &&
                event.source != null
            ) {
                showFloatingWidget(event.source!!)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onInterrupt() {
        // Required override, leave empty
    }

    private fun showFloatingWidget(textEditNode: AccessibilityNodeInfo) {
        // TODO: How to auto-delete the floating widget when we are no longer in the same context?
        // When the rootInActiveWindow changes I guess- might have to listen to another accessibility event.
        suggestionFloatingWidgetManager =
            SuggestionFloatingWidgetManager(this, rootInActiveWindow, textEditNode)
        suggestionFloatingWidgetManager?.showWidget()
    }

    override fun onDestroy() {
        super.onDestroy()
        suggestionFloatingWidgetManager?.removeWidget()
    }

}
