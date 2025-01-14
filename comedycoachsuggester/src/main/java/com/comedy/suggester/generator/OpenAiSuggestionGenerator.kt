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
                Config.OPEN_AI_MODEL, prompt,
                llmResponse
            )
        )
    }

    private fun createLlmRequest(prompt: String): ChatCompletionRequest {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(Config.OPEN_AI_MODEL),
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


