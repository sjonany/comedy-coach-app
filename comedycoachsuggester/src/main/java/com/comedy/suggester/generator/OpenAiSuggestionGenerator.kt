package com.comedy.suggester.generator

import android.util.Log
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.comedy.suggester.Config
import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile

/**
 * Generates suggestions using OpenAI
 */
class OpenAiSuggestionGenerator(val apiClient: OpenAI) : SuggestionGenerator {
    companion object {
        private const val LOG_TAG = "OpenAiSuggestionGenerator"
    }

    override suspend fun generateSuggestions(
        chatMessages: ChatMessages,
        userHint: String,
        characterProfilesById: Map<String, CharacterProfile>
    ): SuggestionResult? {
        // Construct prompt
        val prompt =
            PromptStrings.suggestionGenerationPrompt(chatMessages, userHint, characterProfilesById)
        val llmRequest = createLlmRequest(prompt)

        // Ask LLM
        val chatChoices: List<ChatChoice> = apiClient.chatCompletion(llmRequest).choices
        Log.d(LOG_TAG, "chatChoices: $chatChoices")
        if (chatChoices.isEmpty()) {
            return null
        }

        // Just use the first chat choice for now
        val llmResponse = chatChoices[0].message.content ?: return null
        val suggestions = parseLlmResponse(llmResponse)
        if (suggestions.isEmpty()) {
            return null
        }
        return SuggestionResult(
            suggestions, GenerationMetadata(
                Config.LLM_MODEL, prompt,
                llmResponse
            )
        )
    }

    val SUGGESTION_PREFIX = "-"

    /**
     * We assume the llm response to be a repetition of the following segments:
     * Some kind of text \n
     * - suggestion 1 \n
     * - suggestion 2 \n
     * ending text
     *
     * And, we just want the final segment.
     * So, we look for the last hyphenated list item, and just keep going up until we hit a non-list
     * item
     */
    internal fun parseLlmResponse(llmResponse: String): List<String> {
        val tokens = llmResponse.split("\n")
        val result: MutableList<String> = mutableListOf()
        // Go in reverse order
        for (tok in tokens.reversed()) {
            var curTok = tok.trim()
            if (!curTok.startsWith(SUGGESTION_PREFIX)) {
                if (result.isNotEmpty()) {
                    // The end of a hyphenated segment.
                    break
                }
                continue
            }
            curTok = curTok.removePrefix(SUGGESTION_PREFIX).trim()
            result.add(curTok)
        }

        if (result.isEmpty()) {
            // Sometimes the LLM just gives a one-element suggestion.
            result.add(llmResponse.trim())
        }
        return result.reversed()
    }

    private fun createLlmRequest(prompt: String): ChatCompletionRequest {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(Config.LLM_MODEL),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        Log.d(
            LOG_TAG, "Prompt: $prompt"
        )
        return chatCompletionRequest
    }
}

// Trim lines for each line in the string.
internal fun trimLines(input: String): String {
    return input.lines().joinToString("\n") { it.trim() }
}

