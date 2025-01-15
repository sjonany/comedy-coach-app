package com.comedy.suggester.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.comedy.suggester.data.GeneratedSuggestions.Companion.TABLE_NAME

/**
 * Data model for storing all suggestion generations.
 */
@Entity(tableName = TABLE_NAME)
data class GeneratedSuggestions(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // When the generation happened. In millisecond since epoch,
    val timestamp: Long,

    // The LLM model used
    val modelName: String,

    // The full prompt fed into the LLM
    val prompt: String,

    // The raw response from the LLM. This contains chain of thought output,
    // as well as the 5 suggestions
    val response: String,

    // What user chose as the response. This will be null if the user hasn't chosen a response.
    // This is one of the 5 suggestions, and so should be a substring of 'response'
    val chosenResponse: String?
) {
    companion object {
        const val TABLE_NAME = "generated_suggestions"
    }
}