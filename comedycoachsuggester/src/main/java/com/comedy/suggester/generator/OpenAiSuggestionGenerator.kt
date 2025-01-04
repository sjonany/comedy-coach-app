package com.comedy.suggester.generator

import android.util.Log
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.comedy.suggester.chatparser.ChatMessages

/**
 * Generates suggestions using OpenAI
 */
class OpenAiSuggestionGenerator(val apiClient: OpenAI) : SuggestionGenerator {
    companion object {
        private const val LOG_TAG = "OpenAiSuggestionGenerator"

        // See https://platform.openai.com/docs/models
        private const val LLM_MODEL = "gpt-4-turbo"
        //"gpt-3.5-turbo"
    }

    override suspend fun generateSuggestions(
        chatMessages: ChatMessages,
        userHint: String
    ): SuggestionResult? {
        // Construct prompt
        val llmRequest = createLlmRequest(chatMessages, userHint)

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
        return SuggestionResult(suggestions, GenerationMetadata(LLM_MODEL))
    }

    val SUGGESTION_PREFIX = "-"

    /**
     * We assume the llm response looks like this:
     * Some kind of text \n
     * - suggestion 1 \n
     * - suggestion 2 \n
     * ending text
     */
    internal fun parseLlmResponse(llmResponse: String): List<String> {
        val tokens = llmResponse.split("\n")
        val result: MutableList<String> = mutableListOf()
        for (tok in tokens) {
            var curTok = tok.trim()
            if (!curTok.startsWith(SUGGESTION_PREFIX)) {
                continue
            }
            curTok = curTok.removePrefix(SUGGESTION_PREFIX).trim()
            result.add(curTok)
        }

        if (result.isEmpty()) {
            // Sometimes the LLM just gives a one-element suggestion.
            result.add(llmResponse.trim())
        }
        return result
    }

    fun createLlmRequest(chatMessages: ChatMessages, userHint: String): ChatCompletionRequest {
        val prompt = chatMessagesToPrompt(chatMessages, userHint)
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(LLM_MODEL),
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

    internal fun chatMessagesToPrompt(chatMessages: ChatMessages, userHint: String): String {
        chatMessages.obfuscateSenders()
        val chatMessagePromptPart =
            chatMessages.getMessages().joinToString(separator = "\n") { message ->
                "${message.sender}: ${message.message}"
            }

        return "Please suggest 5 funny responses to this chat history, " +
                "with hyphen as bullet points and separated by newline " +
                "(E.g. - Content1\n - Content2). " +
                (if (userHint.trim()
                        .isEmpty()
                ) "" else "Also, $userHint.\n") +
                "Do NOT include the person name in your response.\n" +
                "Chat history:\n$chatMessagePromptPart"
    }
}