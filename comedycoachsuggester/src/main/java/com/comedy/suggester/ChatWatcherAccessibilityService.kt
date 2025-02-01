package com.comedy.suggester

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.comedy.suggester.data.AppContainer
import com.comedy.suggester.ui.common.showText
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
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val MESSENGER_PACKAGE = "com.facebook.orca"

        // If the user types this sequence, then the generator widget will show up.
        const val TURN_ON_PREFIX = "Qw"

        private val HANDLED_PACKAGES = setOf(
            DISCORD_PACKAGE, WHATSAPP_PACKAGE, INSTAGRAM_PACKAGE, MESSENGER_PACKAGE
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
        CoroutineScope(Dispatchers.IO).launch {
            appContainer.appSettingsRepository.getMainSettings().collect { mainSettings ->
                if (mainSettings == null) {
                    showText(
                        applicationContext, "App settings aren't configured yet." +
                                " Please open the comedy coach suggester app, then re-enable and disable" +
                                " the accessibility part of this app."
                    )
                    return@collect
                }
                appContainer.initAppSettings(mainSettings)
                val openAiApiKey = mainSettings.openAiApiKey
                appContainer.initOpenAiApiService(openAiApiKey)
                val anthropicApiKey = mainSettings.anthropicApiKey
                appContainer.initAnthropicClient(anthropicApiKey)
                isServiceReady = true
                Log.d(
                    LOG_TAG,
                    "Service is initialized with settings = $mainSettings"
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
            "got event: $eventTypeString, $packageName, ${event.className}"
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

        if ((
                    // Discord, Whatsapp
                    event.className == "android.widget.EditText" ||
                            // Instagram
                            event.className == "android.widget.AutoCompleteTextView"
                    ) &&
            event.source != null &&
            event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
        ) {
            // Typing triggers two things:
            // (1) hint that will be used as part of the prompt
            // (2) if it follows the TURN_ON_PREFIX, then we want to turn on the widget
            //    I don't auto-turn on the widget on text focus because some apps like whatsapp
            //    auto-focuses when you click on a thread, and it gets kinda annoying
            val userString = event.source?.text.toString()

            suggestionGeneratorWidget?.userHint = userString
            Log.d(LOG_TAG, "User hint updated to ${suggestionGeneratorWidget?.userHint}")

            if (suggestionGeneratorWidget?.isLive != true && userString.startsWith(TURN_ON_PREFIX)) {
                redrawShowSuggestionWidget(event.source!!, packageName)
                return
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
        packageName: String
    ) {
        Log.d(LOG_TAG, "redrawShowSuggestionWidget")
        suggestionGeneratorWidget?.destroyWidget()
        suggestionGeneratorWidget =
            SuggestionGeneratorWidget(this, rootInActiveWindow, textEditNode, packageName)
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
