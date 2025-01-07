package com.comedy.suggester.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for CharacterProfile's
 */
interface CharacterProfileRepository {

    fun findCharacterProfileById(characterId: String): Flow<CharacterProfile?>

    fun getAllCharacterProfiles(): Flow<List<CharacterProfile>>

    suspend fun createNewProfile(profile: CharacterProfile)

    suspend fun updateProfile(profile: CharacterProfile)

    /** Find a character by the package-specific alias. If there is multiple, we just return one
     * arbitrarily.
     */
    fun findCharacterProfileByAlias(packageName: String, alias: String): Flow<CharacterProfile?>
}