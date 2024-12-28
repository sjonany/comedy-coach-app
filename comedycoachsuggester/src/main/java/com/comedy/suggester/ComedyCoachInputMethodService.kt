package com.comedy.suggester

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import com.comedy.suggester.ui.keyboard.CustomKeyboardView

class ComedyCoachInputMethodService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboard: Keyboard
    private lateinit var keyboardView: CustomKeyboardView


    override fun onCreate() {
        super.onCreate()
        keyboard = Keyboard(this, R.xml.qwerty_keyboard)
    }

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard, null) as CustomKeyboardView
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView
    }
    
    // Implement required KeyboardView.OnKeyboardActionListener methods
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        // Handle key press
        currentInputConnection.commitText(primaryCode.toChar().toString(), 1)
    }

    override fun onPress(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onRelease(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onText(p0: CharSequence?) {
        TODO("Not yet implemented")
    }

    override fun swipeLeft() {
        TODO("Not yet implemented")
    }

    override fun swipeRight() {
        TODO("Not yet implemented")
    }

    override fun swipeDown() {
        TODO("Not yet implemented")
    }

    override fun swipeUp() {
        TODO("Not yet implemented")
    }
}
