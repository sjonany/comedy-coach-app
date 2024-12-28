package com.comedy.suggester.ui.keyboard

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.view.KeyEvent

class CustomKeyboardView constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KeyboardView(context, attrs) {
    // TODO: Why keyboard view is deprecated?
    // Custom behavior for key press can be added here
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Handle specific key actions if needed
        return super.onKeyDown(keyCode, event)
    }

    // Additional customization methods
}