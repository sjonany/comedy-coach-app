package com.comedy.suggester.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for CharacterProfile
 */
@Dao
interface CharacterProfileDao {
    @Query("SELECT * FROM ${CharacterProfile.TABLE_NAME} WHERE id = :characterId")
    fun findCharacterProfileById(characterId: String): Flow<CharacterProfile?>

    @Query("SELECT * FROM ${CharacterProfile.TABLE_NAME}")
    fun getAllCharacterProfiles(): Flow<List<CharacterProfile>>

    // If a profile already exists, we abort the insert.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createNewProfile(profile: CharacterProfile)

    @Update
    suspend fun updateProfile(profile: CharacterProfile)
}