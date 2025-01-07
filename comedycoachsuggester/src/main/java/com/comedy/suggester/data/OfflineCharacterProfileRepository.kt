package com.comedy.suggester.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of character profile repo where we use the DAO
 */
class OfflineCharacterProfileRepository(private val characterProfileDao: CharacterProfileDao) :
    CharacterProfileRepository {
    override fun findCharacterProfileById(characterId: String): Flow<CharacterProfile?> =
        characterProfileDao.findCharacterProfileById(characterId)

    override fun getAllCharacterProfiles(): Flow<List<CharacterProfile>> =
        characterProfileDao.getAllCharacterProfiles()

    override suspend fun createNewProfile(profile: CharacterProfile) =
        characterProfileDao.createNewProfile(profile)

    override suspend fun updateProfile(profile: CharacterProfile) =
        characterProfileDao.updateProfile(profile)

    override
    fun findCharacterProfileByAlias(packageName: String, alias: String): Flow<CharacterProfile?> =
        // TODO: Optimize the alias search. Rn it re-fetches all the character profiles.
        getAllCharacterProfiles().map { profiles ->
            profiles.find { profile: CharacterProfile ->
                profile.aliases[packageName]?.any {
                    it == alias
                } == true
            }
        }
}