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


/**
 * A widget that gets the chat context, and displays a list of suggestions.
 * Triggered by [SuggestionFloatingWidgetManager]
 */
class SuggestionResultWidgetManager(
    private val context: Context,
    private val rootInActiveWindow: AccessibilityNodeInfo,
    private val editTextNode: AccessibilityNodeInfo
) {
    companion object {
        private const val LOG_TAG = "SuggestionResultWidgetManager"
    }

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    // TODO: Fetch chat context, call LLM. Right now it just shows hardcoded suggestions.
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

        // Set listeners for the buttons
        val option1 = floatingView!!.findViewById<Button>(R.id.option1)
        val option2 = floatingView!!.findViewById<Button>(R.id.option2)
        val option3 = floatingView!!.findViewById<Button>(R.id.option3)
        val finishButton = floatingView!!.findViewById<Button>(R.id.finish)

        option1.setOnClickListener { replaceText(editTextNode, "Option 1 Selected") }
        option2.setOnClickListener { replaceText(editTextNode, "Option 2 Selected") }
        option3.setOnClickListener { replaceText(editTextNode, "Option 3 Selected") }
        finishButton.setOnClickListener { removeWidget() }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )


        // Set the y-position of the floating view to place it below the editText
        // Ideally I will place it to cover the keyboard, but idk how to get the keyboard location
        // easily
        params.x = 0
        // Display it over the keyboard
        val editTextBound = Rect()
        editTextNode.getBoundsInScreen(editTextBound)
        params.y = editTextBound.bottom
        params.gravity =
            Gravity.BOTTOM or Gravity.START // Position the view at the bottom left

        // TODO: Make the floating window consume the same space as keyboard, but it should be
        // scrollable if it has too much content
        // Add the floating view to the window manager
        windowManager!!.addView(floatingView, params)
    }

    fun removeWidget() {
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