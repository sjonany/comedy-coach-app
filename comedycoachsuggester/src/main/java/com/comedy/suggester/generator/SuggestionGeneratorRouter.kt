package com.comedy.suggester.generator

import com.aallam.openai.client.OpenAI
import com.anthropic.client.AnthropicClient
import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile
import com.comedy.suggester.data.LlmModel


/**
 * Suggestion generator that picks model according to the user's preference.
 */
class SuggestionGeneratorRouter(
    private val chosenModel: LlmModel,
    anthropicClient: AnthropicClient,
    openAiClient: OpenAI
) : SuggestionGenerator {
    companion object {
        private const val LOG_TAG = "SuggestionGeneratorRouter"
    }

    private val anthropicGenerator = AnthropicSuggestionGenerator(anthropicClient)
    private val openAiGenerator = OpenAiSuggestionGenerator(openAiClient)

    override suspend fun generateSuggestions(
        chatMessages: ChatMessages,
        userHint: String,
        characterProfilesById: Map<String, CharacterProfile>
    ): SuggestionResult? {
        return when (chosenModel) {
            LlmModel.GPT_4_TURBO ->
                openAiGenerator.generateSuggestions(
                    "gpt-4-turbo-2024-04-09",
                    chatMessages, userHint, characterProfilesById
                )

            LlmModel.GPT_4O_MINI ->
                openAiGenerator.generateSuggestions(
                    "gpt-4o-mini-2024-07-18",
                    chatMessages, userHint, characterProfilesById
                )

            LlmModel.CLAUDE_3_5_SONNET ->
                anthropicGenerator.generateSuggestions(
                    "claude-3-5-sonnet-20241022",
                    chatMessages, userHint, characterProfilesById
                )

            LlmModel.CLAUDE_3_5_HAIKU ->
                anthropicGenerator.generateSuggestions(
                    "claude-3-5-haiku-20241022",
                    chatMessages, userHint, characterProfilesById
                )

        }
    }
}
