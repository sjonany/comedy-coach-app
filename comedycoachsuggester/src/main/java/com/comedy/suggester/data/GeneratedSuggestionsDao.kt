package com.comedy.suggester.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * DAO for GeneratedSuggestions.
 * We're not supporting any reads, because I want to read these on the computer anyway.
 * Just use android studio and access the roomdb entries.
 */
@Dao
interface GeneratedSuggestionsDao {
    // If a profile already exists, we abort the insert.
    // The return value is the auto-generated ID of the created row
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNewGeneratedSuggestion(suggestion: GeneratedSuggestions): Long

    @Update
    suspend fun updateGeneratedSuggestion(suggestion: GeneratedSuggestions)
}