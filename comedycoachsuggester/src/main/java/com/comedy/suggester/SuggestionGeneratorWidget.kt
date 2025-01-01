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


/**
 * Draws the floating widget in chat text fields that will trigger response generation.
 * The instance is 1-1 tied to a chat context. If the chat context changes, the caller should
 * destroy this instance and create a new one.
 */
class SuggestionGeneratorWidget(
    private val context: Context,
    private val rootInActiveWindow: AccessibilityNodeInfo,
    private val textEditNode: AccessibilityNodeInfo
) {

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

    /**
     * Draws the widget on the screen, and attach a click listener.
     * This should only be called once per class instance, during construction.
     */
    fun drawWidget() {
        Log.d(LOG_TAG, "drawWidget")

        // Draw the floating widget
        drawFloatingWidget()

        // Attach listener to the buttons.
        val generateSuggestionsButton =
            widgetView!!.findViewById<Button>(R.id.generateSuggestionsButton)
        generateSuggestionsButton.setOnClickListener() {
            Log.d(LOG_TAG, "Generate suggestion button clicked")
            // Recreate the suggestion widget
            suggestionResultsWidget?.destroyWidget()
            // TODO: Get chat context, call llm etc. Create a suggestion generator class here, chat
            // parsing context etc. Optionally turn into a loading gear
            // Only when the heavy lifting is done with do we proceed w/ calling the result manager
            suggestionResultsWidget =
                SuggestionResultsWidget(context, rootInActiveWindow, textEditNode)
            suggestionResultsWidget!!.showWidget()
        }

        val closeButton = widgetView!!.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener() {
            Log.d(LOG_TAG, "Close button clicked")
            destroyWidget()
        }
    }

    // Draw the floating widget on the screen. There's a show suggestion button on there.
    private fun drawFloatingWidget() {
        Log.d(LOG_TAG, "drawFloatingWidget")
        assert(widgetView == null)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        widgetView = layoutInflater.inflate(R.layout.floating_widget, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
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
        params.width = 700
        params.height = 100

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
    }
}