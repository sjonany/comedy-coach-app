package com.comedy.suggester.data

/**
 * Implementation of character profile repo where we use the DAO
 */
class OfflineGeneratedSuggestionsRepository(private val generatedSuggestionsDao: GeneratedSuggestionsDao) :
    GeneratedSuggestionsRepository {

    override suspend fun insertNewGeneratedSuggestion(suggestion: GeneratedSuggestions): Long {
        return generatedSuggestionsDao.insertNewGeneratedSuggestion(suggestion)
    }

    override suspend fun updateGeneratedSuggestion(suggestion: GeneratedSuggestions) {
        generatedSuggestionsDao.updateGeneratedSuggestion(suggestion)
    }
}