package com.comedy.suggester.generator

import com.comedy.suggester.chatparser.ChatMessages

// Generates suggestions given chat context
interface SuggestionGenerator {
    suspend fun generateSuggestions(chatMessages: ChatMessages): SuggestionResult?
}

// Suggestion result. Contains the suggestions and metadata on how they were generated
data class SuggestionResult(
    val suggestions: List<String>,
    val generationMetadata: GenerationMetadata
)

// Metadata on how the suggestions were generated
data class GenerationMetadata(
    // The llm model used for this generation
    val modelName: String
)
