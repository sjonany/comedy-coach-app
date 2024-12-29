package com.comedy.suggester

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button


/**
 * Draws the floating widget in chat text fields that will trigger response generation.
 */
class SuggestionFloatingWidgetManager(
    private val context: Context,
    private val rootInActiveWindow: AccessibilityNodeInfo,
    private val textEditNode: AccessibilityNodeInfo
) {
    private var suggestionResultWidgetManager: SuggestionResultWidgetManager? = null
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    fun showWidget() {
        if (floatingView != null) return // Already shown

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = layoutInflater.inflate(R.layout.floating_widget, null)


        val showSuggestionsButton = floatingView!!.findViewById<Button>(R.id.showSuggestionsButton)
        showSuggestionsButton.setOnClickListener() {
            Log.d("SuggestionFloatingWidgetManager", "Show suggestion button clicked")
            suggestionResultWidgetManager =
                SuggestionResultWidgetManager(context, rootInActiveWindow, textEditNode)
            suggestionResultWidgetManager!!.showWidget()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Position the floating widget over the EditText
        val textEditBounds = Rect()
        textEditNode.getBoundsInScreen(textEditBounds)
        if (textEditBounds.height() == 0) {
            return
        }
        params.x = textEditBounds.left
        params.y = textEditBounds.top
        params.width = textEditBounds.width()
        params.height = textEditBounds.height()
        params.gravity = Gravity.TOP or Gravity.START

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.addView(floatingView, params)
    }

    fun removeWidget() {
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
        suggestionResultWidgetManager?.removeWidget()
    }
}