package com.comedy.suggester.data

/**
 * Repository for GeneratedSuggestions
 */
interface GeneratedSuggestionsRepository {

    // The return value is the auto-generated ID of the created row
    suspend fun insertNewGeneratedSuggestion(suggestion: GeneratedSuggestions): Long

    suspend fun updateGeneratedSuggestion(suggestion: GeneratedSuggestions)
}