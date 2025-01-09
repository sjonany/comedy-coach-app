package com.comedy.suggester.generator

import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile

// Generates suggestions given chat context
interface SuggestionGenerator {
    suspend fun generateSuggestions(
        chatMessages: ChatMessages,
        userHint: String,
        characterProfilesById: Map<String, CharacterProfile>
    ): SuggestionResult?
}

// Suggestion result. Contains the suggestions and metadata on how they were generated
data class SuggestionResult(
    val suggestions: List<String>,
    val generationMetadata: GenerationMetadata,
)

// Metadata on how the suggestions were generated
data class GenerationMetadata(
    // The llm model used for this generation
    val modelName: String,
    // The prompt used for the generation
    val prompt: String,
    // The raw llm response that gets parsed into suggestions
    val llmResponse: String
)
