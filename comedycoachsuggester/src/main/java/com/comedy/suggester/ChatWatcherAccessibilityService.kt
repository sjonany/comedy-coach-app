package com.comedy.suggester

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo


/**
 * This service watches for chat apps, and has a button that will trigger
 * ResponseSuggestionInputMethodService
 */
class ChatWatcherAccessibilityService : AccessibilityService() {
    private var suggestionGeneratorWidget: SuggestionGeneratorWidget? = null

    companion object {
        private const val LOG_TAG = "ChatWatcherAccessibilityService"
        private const val DISCORD_PACKAGE = "com.discord"

        // Event types that indicate that the user clicked on an edit text field
        private val TEXT_EDIT_FOCUS_EVENT_TYPES = setOf(
            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_VIEW_FOCUSED,
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(
            LOG_TAG,
            "got event: $event"
        )
        if (event == null) return
        // Need overlay permission first. The settings activity handles all these
        if (!Settings.canDrawOverlays(this)) {
            Log.d(
                LOG_TAG,
                "can't draw overlays, starting settings activity"
            )
            val intent = Intent(this, ComedyCoachIMESettingsActivity::class.java)
            startActivity(intent)
            return
        }

        val eventTypeString = AccessibilityEvent.eventTypeToString(event.eventType)
        val packageName = event.packageName?.toString()

        Log.d(
            LOG_TAG,
            "is keyboard visible = ${isKeyboardVisible()}, got event: $packageName $eventTypeString"
        )

        if (packageName == DISCORD_PACKAGE) {
            handleDiscordEvent(event)
            return
        }

        assert(packageName != DISCORD_PACKAGE)
        // Assume we have navigated away from discord. Destroy the suggestion windows.
        // TODO: Not reliable, even if I added the following to the manifest.
        // Dunno how to catch back swipe, and keyboard visibility isn't reliable too.
        // Ugh, let's just rely on a manual cancel button.
        // "android:packageNames="com.discord,com.android.activitymanager,com.android.launcher,com.android.systemui"
        destroyShowSuggestionWidget()
    }

    private fun handleDiscordEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val eventTypeString = AccessibilityEvent.eventTypeToString(event.eventType)
        // Show widget if we focus on an edit text
        if (event.eventType in TEXT_EDIT_FOCUS_EVENT_TYPES &&
            event.className == "android.widget.EditText" &&
            event.source != null
        ) {
            redrawShowSuggestionWidget(event.source!!)
            return
        }
        // Hide widget if the event causes the keyboard to no longer be visible
        // TODO: isKeyboardVisible isn't reliable unfortunately :/
        // The workaround is for me to create a button to manually close the widget.
        // But eh, scrolling around does the discord does work so let's just rely on that.
        if (suggestionGeneratorWidget != null) {
            Log.d(LOG_TAG, "While widget manager is visible, the event: $eventTypeString")
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                    -> {
                    val isKeyboardVisible = isKeyboardVisible()
                    Log.d(
                        LOG_TAG,
                        "While widget manager is visible, isKeyboardVisible = $isKeyboardVisible, the event to destroy: $event"
                    )

                    if (!isKeyboardVisible) {
                        destroyShowSuggestionWidget()
                    }
                }

                else -> {
                    Log.d(
                        LOG_TAG,
                        "While widget manager is visible, the event, but won't destroy: $event"
                    )
                }
            }
        }
    }

    override fun onInterrupt() {
        // Required override, leave empty
    }

    // TODO: Not reliable :/ Idk still an open problem.
    private fun isKeyboardVisible(): Boolean {
        for (window in windows) {
            if (window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                /*
                // Bounds are NOT reliable. Even when the keyboard isn't visible, we still get a
                // normal looking bound.
                val bounds = Rect()
                window.getBoundsInScreen(bounds)
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels

                // Check if the keyboard occupies a significant portion of the screen
                Log.d(
                    LOG_TAG,
                    "isKeyboardVisible. Height = ${bounds.height()}, Bottom = ${bounds.bottom}, Top = ${bounds.top}"
                )
                return bounds.height() > screenHeight / 3
                 */
                return true
            }
        }
        return false
    }

    /**
     * Recreate the show-suggestion widget. There can only be one that's active at any time.
     * The widget's lifetime is bound to the text edit node's visibility.
     *
     * Example caller:
     * - when user focuses on an edittext - but this is not called multiple times as user is typing.
     *
     * Example life cycles:
     * - If user focuses on a different chat context, then we want to recreate the show-suggestion
     * widget from scratch, because it's tied to a specific text edit node.
     */
    private fun redrawShowSuggestionWidget(textEditNode: AccessibilityNodeInfo) {
        Log.d(LOG_TAG, "redrawShowSuggestionWidget")
        suggestionGeneratorWidget?.destroyWidget()
        suggestionGeneratorWidget =
            SuggestionGeneratorWidget(this, rootInActiveWindow, textEditNode)
        suggestionGeneratorWidget!!.drawWidget()
    }

    /**
     * Destroy the show-suggestion widget.
     *
     * Example caller:
     * - when user switches app, or loses focus on the edit text and therefore hides the keyboard.
     */
    private fun destroyShowSuggestionWidget() {
        Log.d(LOG_TAG, "destroyShowSuggestionWidget")
        suggestionGeneratorWidget?.destroyWidget()
        suggestionGeneratorWidget = null
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
        super.onDestroy()
        destroyShowSuggestionWidget()
    }

}
