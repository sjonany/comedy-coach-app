package com.comedy.suggester

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import com.comedy.suggester.chatparser.ChatMessage
import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.chatparser.ChatParserFactory
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.generator.OpenAiSuggestionGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


/**
 * Draws the floating widget in chat text fields that will trigger response generation.
 * The instance is 1-1 tied to a chat context. If the chat context changes, the caller should
 * destroy this instance and create a new one.
 * @param packageName - the package name of the app that triggered the generation
 */
class SuggestionGeneratorWidget(
    private val context: Context,
    private val rootInActiveWindow: AccessibilityNodeInfo,
    private val textEditNode: AccessibilityNodeInfo,
    private val packageName: String
) {
    private val chatParser = ChatParserFactory.getChatParser(packageName)

    companion object {
        // For some reason whenever we windowManager.addView, the y offset that I have is further
        // incremented by this amount before rendering.
        // Maybe something about the status bar is being further added to the y offset.
        const val LAYOUT_DRAW_OFFSET = 150
        private const val LOG_TAG = "SuggestionGeneratorWidget"
    }

    private var suggestionResultsWidget: SuggestionResultsWidget? = null
    private var windowManager: WindowManager? = null

    // the widget that when clicked, will trigger suggestion generation.
    private var widgetView: View? = null
    private val suggestionGenerator =
        OpenAiSuggestionGenerator((context.applicationContext as SuggesterApplication).container.openAiApiService!!)
    private val characterProfileRepository =
        (context.applicationContext as SuggesterApplication).container.characterProfileRepository

    // Whether or not we're waiting for suggestion generation to happen. Only at most one can be
    // active at any time
    @Volatile
    private var isGenerating = false

    // User hint is continuously updated by ChatWatcherAccessibilityService
    // Can't just fetch from the original edit text node because that view is out of date
    var userHint: String = ""

    // drawWidget turns it to true, destroy turns it to false
    var isLive: Boolean = false

    /**
     * Draws the widget on the screen, and attach a click listener.
     * This should only be called once per class instance, during construction.
     */
    fun drawWidget() {
        Log.d(LOG_TAG, "drawWidget")

        // Draw the floating widget
        drawFloatingWidget()

        // Attach listener to the buttons.
        val closeButton = widgetView!!.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener() {
            Log.d(LOG_TAG, "Close button clicked")
            destroyWidget()
        }
        val generateSuggestionsButton =
            widgetView!!.findViewById<Button>(R.id.generateSuggestionsButton)
        generateSuggestionsButton.setOnClickListener() {
            Log.d(LOG_TAG, "Generate suggestion button clicked")

            if (isGenerating) {
                Log.d(LOG_TAG, "Still waiting for suggestion generation...")
                return@setOnClickListener
            }
            generateSuggestionsButton.setText("Generating...")
            isGenerating = true

            // Recreate the suggestion widget
            suggestionResultsWidget?.destroyWidget()

            // Parse chat
            val chatMessages: ChatMessages =
                chatParser.parseChatFromRootNode(rootInActiveWindow)
            Log.d(LOG_TAG, "Parsed chat messages: $chatMessages")

            val uniqueSenderAliases = chatMessages.getMessages().map { it.sender }.toSet()

            // Generate responses using LLM
            CoroutineScope(Dispatchers.Main).launch {
                // Fetch the relevant character profiles
                val aliasToProfile: Map<String, CharacterProfile?> =
                    uniqueSenderAliases.associateWith { senderAlias ->
                        characterProfileRepository.findCharacterProfileByAlias(
                            packageName,
                            senderAlias
                        ).firstOrNull()
                    }
                Log.d(LOG_TAG, "Fetched character profiles: $aliasToProfile")

                // Normalize the names in the chat messages
                val senderNormalizedChatMessages: MutableList<ChatMessage> =
                    chatMessages.getMessages().toMutableList()
                senderNormalizedChatMessages.replaceAll { message ->
                    val normalizedName = aliasToProfile[message.sender]?.id ?: message.sender
                    message.copy(sender = normalizedName)
                }
                val characterProfilesById: Map<String, CharacterProfile> =
                    aliasToProfile.values.filterNotNull().associateBy { it.id }

                Log.d(LOG_TAG, "characterProfilesById: $characterProfilesById")
                Log.d(LOG_TAG, "Normalized chat senders: $senderNormalizedChatMessages")
                val suggestions =
                    suggestionGenerator.generateSuggestions(
                        ChatMessages(
                            senderNormalizedChatMessages
                        ), userHint,
                        characterProfilesById
                    )
                if (suggestions != null) {
                    Log.d(
                        LOG_TAG,
                        "Generated suggestions: $suggestions"
                    )
                    // Only when the heavy lifting is done with do we proceed w/ calling the result manager
                    suggestionResultsWidget =
                        SuggestionResultsWidget(
                            context,
                            rootInActiveWindow,
                            textEditNode,
                            suggestions
                        )
                    suggestionResultsWidget!!.showWidget()
                }
                generateSuggestionsButton.setText(R.string.generate_suggestions)
                isGenerating = false
            }
        }

        isLive = true
    }

    // Draw the floating widget on the screen. There's a show suggestion button on there.
    private fun drawFloatingWidget() {
        Log.d(LOG_TAG, "drawFloatingWidget")
        assert(widgetView == null)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        widgetView = layoutInflater.inflate(R.layout.floating_widget, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        // Idk something about the accessibility service giving non-trustworthy coordinates
        // If I want to fix this: Maybe better to just see if keyboard is visible, and place it
        // relative to that
        params.y = 1500 - LAYOUT_DRAW_OFFSET
        params.height = 150

        Log.d(LOG_TAG, "Floating widget params: $params")
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.addView(widgetView, params)
    }

    /**
     * Remove the UI component and all the memory commitments of this widget.
     * Should be called by containing parent's onDestroy()
     */
    fun destroyWidget() {
        if (widgetView != null) {
            windowManager?.removeView(widgetView)
            widgetView = null
        }
        suggestionResultsWidget?.destroyWidget()
        isLive = false
    }
}