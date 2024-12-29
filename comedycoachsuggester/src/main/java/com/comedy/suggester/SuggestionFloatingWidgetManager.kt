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

    companion object {
        // For some reason whenever we windowManager.addView, the y offset that I have is further
        // incremented by this amount before rendering.
        // Maybe something about the status bar is being further added to the y offset.
        const val LAYOUT_DRAW_OFFSET = 150
    }

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
            if (suggestionResultWidgetManager == null) {
                suggestionResultWidgetManager =
                    SuggestionResultWidgetManager(context, rootInActiveWindow, textEditNode)
            }
            suggestionResultWidgetManager!!.showWidget()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Position the floating widget below the edit text
        val textEditBounds = Rect()
        // TODO: Sometimes this is off? The starting y offset is > end y offset, and so the show
        // suggestions button is shown at the top.
        textEditNode.getBoundsInScreen(textEditBounds)
        if (textEditBounds.height() == 0) {
            return
        }
        params.gravity = Gravity.TOP or Gravity.START
        params.x = textEditBounds.left
        // Idk something about the accessibility service giving non-trustworthy coordinates
        // If I want to fix this: Maybe better to just see if keyboard is visible, and place it
        // relative to that
        params.y = 1500 - LAYOUT_DRAW_OFFSET
        params.width = textEditBounds.width()
        params.height = textEditBounds.height()

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