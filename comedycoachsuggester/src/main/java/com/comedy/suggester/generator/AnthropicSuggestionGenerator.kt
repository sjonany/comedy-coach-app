package com.comedy.suggester.generator

import android.util.Log
import com.anthropic.client.AnthropicClient
import com.anthropic.models.MessageCreateParams
import com.anthropic.models.MessageParam
import com.comedy.suggester.Config
import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile


/**
 * Generates suggestions using Claude
 */
class AnthropicSuggestionGenerator(private val client: AnthropicClient) : SuggestionGenerator {
    companion object {
        private const val LOG_TAG = "ClaudeSuggestionGenerator"
    }

    override suspend fun generateSuggestions(
        chatMessages: ChatMessages,
        userHint: String,
        characterProfilesById: Map<String, CharacterProfile>
    ): SuggestionResult? {
        val prompt =
            PromptStrings.suggestionGenerationPrompt(chatMessages, userHint, characterProfilesById)
        val response = sendLlmRequest(client, prompt) ?: return null
        val suggestions = parseLlmResponse(response)
        if (suggestions.isEmpty()) {
            return null
        }
        return SuggestionResult(
            suggestions, GenerationMetadata(
                Config.CLAUDE_MODEL, prompt,
                response
            )
        )
    }

    fun sendLlmRequest(client: AnthropicClient, prompt: String): String? {
        val request = MessageCreateParams.builder()
            .maxTokens(1024)
            .messages(
                listOf(
                    MessageParam.builder()
                        .role(MessageParam.Role.USER)
                        .content(prompt)
                        .build()
                )
            )
            .model(Config.CLAUDE_MODEL)
            .build()

        return try {
            val response = client.messages().create(request)
            Log.d(LOG_TAG, "Response from anthropic: ${response}")
            response.content().firstOrNull()?.asTextBlock()?.text()
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Error sending request to Anthropic: ${e.message}")
            null
        }
    }
}
