package com.comedy.suggester

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.LinearLayout
import com.comedy.suggester.generator.SuggestionResult


/**
 * A widget that that displays suggested responses.
 * All the heavy lifting (e.g. parse chat context, call LLM) must already be done before creating
 * this class.
 * Triggered by [SuggestionGeneratorWidget]
 */
class SuggestionResultsWidget(
    private val context: Context,
    private val rootInActiveWindow: AccessibilityNodeInfo,
    private val editTextNode: AccessibilityNodeInfo,
    private val suggestionResult: SuggestionResult
) {
    companion object {
        private const val LOG_TAG = "SuggestionResultWidgetManager"
    }

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    fun showWidget() {
        if (floatingView != null) return // Already shown

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (windowManager == null)
            return
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = layoutInflater.inflate(R.layout.suggestions, null)
        if (floatingView == null)
            return

        val linearLayout = floatingView!!.findViewById<LinearLayout>(R.id.linearLayout)

        // Dynamically add buttons for each suggestion
        linearLayout.removeAllViews()
        suggestionResult.suggestions.forEachIndexed { index, suggestion ->
            val button = Button(context)
            button.text = suggestion
            button.setOnClickListener {
                replaceText(editTextNode, suggestion)
            }
            linearLayout.addView(button)
        }
        val closeButton = Button(context)
        closeButton.text = "Back to keyboard"
        closeButton.setOnClickListener { destroyWidget() }
        linearLayout.addView(closeButton)

        val displayMetrics = context.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels // Total screen height
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Situate the widget at the bottom of the screen
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        val editTextBound = Rect()
        editTextNode.getBoundsInScreen(editTextBound)
        // Idk something about the accessibility service giving non-trustworthy coordinates
        // If I want to fix this: Maybe better to just see if keyboard is visible, and place it there.
        // params.y = editTextBound.bottom
        params.y = 1650 - SuggestionGeneratorWidget.LAYOUT_DRAW_OFFSET
        params.height = screenHeight - params.y

        Log.d(LOG_TAG, "Floating widget params: $params")
        // TODO: Make the floating window consume the same space as keyboard, but it should be
        // scrollable if it has too much content
        // Add the floating view to the window manager
        windowManager!!.addView(floatingView, params)
    }

    /**
     * Remove the UI component and all the memory commitments of this widget.
     * Should be called by containing parent's onDestroy()
     */
    fun destroyWidget() {
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
    }

    private fun replaceText(editTextNode: AccessibilityNodeInfo, text: String) {
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )

        val success = editTextNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        Log.d(LOG_TAG, "Replacing text with $text. Success: $success")
    }
}