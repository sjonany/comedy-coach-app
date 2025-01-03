package com.comedy.suggester

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.comedy.suggester.chatparser.ChatParser
import com.comedy.suggester.chatparser.ChatParserFactory
import com.comedy.suggester.data.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This service watches for chat apps, and has a button that will trigger
 * ResponseSuggestionInputMethodService
 */
class ChatWatcherAccessibilityService : AccessibilityService() {
    private var suggestionGeneratorWidget: SuggestionGeneratorWidget? = null

    companion object {
        private const val LOG_TAG = "ChatWatcherAccessibilityService"
        const val DISCORD_PACKAGE = "com.discord"
        const val WHATSAPP_PACKAGE = "com.whatsapp"

        // Event types that indicate that the user clicked on an edit text field
        private val TEXT_EDIT_FOCUS_EVENT_TYPES = setOf(
            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_VIEW_FOCUSED,
        )

        private val HANDLED_PACKAGES = setOf(
            DISCORD_PACKAGE, WHATSAPP_PACKAGE
        )
    }

    /* Global dependencies. */
    private lateinit var appContainer: AppContainer

    /* All event processing must not run until the service is ready - e.g. read api key */
    @Volatile
    private var isServiceReady: Boolean = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        appContainer =
            (applicationContext as SuggesterApplication).container

        // Fully initialize app dependencies. Until this is done, event processing can't start
        CoroutineScope(Dispatchers.Main).launch {
            appContainer.appSettingsRepository.getMainSettings().collect { mainSettings ->
                val openAiApiKey = mainSettings!!.openAiApiKey
                appContainer.initializeOpenAiApiService(openAiApiKey)
                isServiceReady = true
                Log.d(
                    LOG_TAG,
                    "Service is initialized"
                )
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isServiceReady) {
            Log.d(
                LOG_TAG,
                "Still initializing service. Dropping event for now"
            )
            return
        }

        if (event == null) return

        val eventTypeString = AccessibilityEvent.eventTypeToString(event.eventType)
        val packageName = event.packageName?.toString() ?: return

        Log.d(
            LOG_TAG,
            "got event: $eventTypeString, $packageName"
        )

        // Need overlay permission first. The settings activity handles all these
        if (!Settings.canDrawOverlays(this)) {
            Log.d(
                LOG_TAG,
                "can't draw overlays, starting settings activity"
            )
            val intent = Intent(this, ComedyCoachSettingsActivity::class.java)
            startActivity(intent)
            return
        }
        if (!HANDLED_PACKAGES.contains(packageName)) {
            return
        }

        val chatParser = ChatParserFactory.getChatParser(packageName)
        if (event.className == "android.widget.EditText" &&
            event.source != null
        ) {
            if (event.eventType in TEXT_EDIT_FOCUS_EVENT_TYPES) {
                val editTextString = event.text[0].toString()
                if (packageName == DISCORD_PACKAGE) {
                    // This is a workaround to filter for the right edit field.
                    // E.g. we don't want to trigger this on the edittext for emojis
                    // Unfortunately if the edit text field has been edited then this won't trigger
                    // But that's not too bad. User can just erase and redo.
                    if (editTextString.startsWith("Message")) {
                        redrawShowSuggestionWidget(event.source!!, chatParser)
                        return
                    }
                } else if (packageName == WHATSAPP_PACKAGE) {
                    if (editTextString.startsWith("Message")) {
                        redrawShowSuggestionWidget(event.source!!, chatParser)
                        return
                    }
                }
            } else if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                suggestionGeneratorWidget?.userHint = event.source?.text.toString()
                Log.d(LOG_TAG, "User hint updated to ${suggestionGeneratorWidget?.userHint}")
            }
        }

        // TODO: Auto-destroy widget if keyboard is hidden. Unfortunately, this detection mechanism
        // is very brittle. I haven't tried https://stackoverflow.com/a/63517673 though
    }

    override fun onInterrupt() {
        // Required override, leave empty
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
    private fun redrawShowSuggestionWidget(
        textEditNode: AccessibilityNodeInfo,
        chatParser: ChatParser
    ) {
        Log.d(LOG_TAG, "redrawShowSuggestionWidget")
        suggestionGeneratorWidget?.destroyWidget()
        suggestionGeneratorWidget =
            SuggestionGeneratorWidget(this, rootInActiveWindow, textEditNode, chatParser)
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
