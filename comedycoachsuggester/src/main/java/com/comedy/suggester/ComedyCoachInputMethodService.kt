package com.comedy.suggester

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button

private const val ALL_CHARACTERS = 1000

class ComedyCoachInputMethodService : InputMethodService() {

    override fun onCreateInputView(): View {
        // Inflate the custom view with buttons or options
        val inputView = layoutInflater.inflate(R.layout.suggestions, null)

        // Set listeners for the buttons
        val option1 = inputView.findViewById<Button>(R.id.option1)
        val option2 = inputView.findViewById<Button>(R.id.option2)
        val option3 = inputView.findViewById<Button>(R.id.option3)
        val finishButton = inputView.findViewById<Button>(R.id.finish)

        option1.setOnClickListener { replaceText("Option 1 Selected") }
        option2.setOnClickListener { replaceText("Option 2 Selected") }
        option3.setOnClickListener { replaceText("Option 3 Selected") }
        finishButton.setOnClickListener {
            // Close the custom IME, forcing Android to switch to the default IME
            requestHideSelf(0)  // Pass 0 for the flags to hide the IME
        }

        return inputView
    }

    private fun replaceText(text: String) {
        val currentInputConnection = currentInputConnection
        // Clear the text input, then append
        currentInputConnection.deleteSurroundingText(ALL_CHARACTERS, ALL_CHARACTERS)
        currentInputConnection?.commitText(text, 1)
    }
}
