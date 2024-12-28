package com.comedy.suggester

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent

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
                event.className == "android.widget.EditText"
            ) {
                // Get the bounds of the EditText
                val textEditBounds = Rect()
                event.source?.getBoundsInScreen(textEditBounds)

                if (textEditBounds.height() > 0) {
                    // Show floating widget on the EditText
                    showFloatingWidget(textEditBounds)
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        suggestionFloatingWidgetManager = SuggestionFloatingWidgetManager(this)
    }

    override fun onInterrupt() {
        // Required override, leave empty
    }

    private fun showFloatingWidget(textEditBounds: Rect) {
        suggestionFloatingWidgetManager?.showWidget(textEditBounds)
    }

    override fun onDestroy() {
        super.onDestroy()
        suggestionFloatingWidgetManager?.removeWidget()
    }
}
