package com.comedy.suggester

import android.content.Context
import android.content.res.Resources
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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.comedy.suggester.data.GeneratedSuggestions
import com.comedy.suggester.generator.SuggestionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
        private const val DIVIDER = "\n\n------------------------------------\n\n"
    }

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private val generatedSuggestionsRepository =
        (context.applicationContext as SuggesterApplication).container.generatedSuggestionsRepository

    // The DB log entry corresponding to this widget.
    private var suggestionsLog: GeneratedSuggestions? = null

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
            val button = createButton(suggestion)
            button.setOnClickListener {
                replaceText(editTextNode, suggestion)
                // Also log this choice to DB

                if (suggestionsLog != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        suggestionsLog =
                            suggestionsLog!!.copy(chosenResponse = sanitizeStringForCsv(suggestion))
                        generatedSuggestionsRepository.updateGeneratedSuggestion(
                            suggestionsLog!!
                        )
                    }
                }
            }
            linearLayout.addView(button)
        }
        val closeButton = createButton("Back to keyboard")
        closeButton.background =
            ContextCompat.getDrawable(
                context,
                R.drawable.close_button_background
            )
        closeButton.setTextColor(context.getColorStateList(R.color.on_tertiary))
        closeButton.setOnClickListener { destroyWidget() }
        linearLayout.addView(closeButton)

        if (Config.IS_DEBUG) {
            val promptDebugText = TextView(context)
            promptDebugText.text =
                "Debugging information (if you want to see the full prompt)\n\n" +
                        DIVIDER +
                        "Model:\n${suggestionResult.generationMetadata.modelName}\n\n" +
                        DIVIDER +
                        "Prompt:\n${suggestionResult.generationMetadata.prompt}\n\n" +
                        DIVIDER +
                        "Raw LLM response:\n${suggestionResult.generationMetadata.llmResponse}\n\n"
            linearLayout.addView(promptDebugText)

            // Also log to DB
            CoroutineScope(Dispatchers.IO).launch {
                suggestionsLog = GeneratedSuggestions(
                    timestamp = System.currentTimeMillis(),
                    modelName = suggestionResult.generationMetadata.modelName,
                    prompt = sanitizeStringForCsv(suggestionResult.generationMetadata.prompt),
                    response = sanitizeStringForCsv(suggestionResult.generationMetadata.llmResponse),
                    chosenResponse = null
                )
                val newId =
                    generatedSuggestionsRepository.insertNewGeneratedSuggestion(suggestionsLog!!)
                suggestionsLog = suggestionsLog!!.copy(id = newId.toInt())
            }
        }

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

    private fun createButton(buttonText: String): Button {
        return Button(context).apply {
            text = buttonText
            background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
            setTextColor(context.getColorStateList(R.color.on_secondary))
            setPadding(8.dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 8.dp
                rightMargin = 8.dp
                bottomMargin = 8.dp
            }
        }

    }

    // CSV treats \n to signal end of record.
    // If we use \\n, google sheets and libre office will preserve the newlines within the cell.
    // https://datascience.stackexchange.com/a/81361
    // Then on google sheets, create another column like so, so you can see the newlines.
    // =SUBSTITUTE(E2, "\n", char(10))
    private fun sanitizeStringForCsv(str: String): String {
        return str.replace("\n", "\\n")
    }

    val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

}