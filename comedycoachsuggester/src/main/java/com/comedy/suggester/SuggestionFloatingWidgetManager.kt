package com.comedy.suggester

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager


/**
 * Draws the floating widget in chat text fields that will trigger response generation.
 */
class SuggestionFloatingWidgetManager(private val context: Context) {
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    fun showWidget(targetBounds: Rect) {

        if (floatingView != null) return // Already shown

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = layoutInflater.inflate(R.layout.floating_widget, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Position the floating widget over the EditText
        params.x = targetBounds.left
        params.y = targetBounds.top
        params.width = targetBounds.width()
        params.height = targetBounds.height()
        params.gravity = Gravity.TOP or Gravity.START

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.addView(floatingView, params)
    }

    fun removeWidget() {
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
    }
}