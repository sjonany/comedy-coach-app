package com.comedy.suggester.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.comedy.suggester.data.CharacterProfile.Companion.TABLE_NAME

/**
 * Data model for storing details about a character profile
 */
@Entity(tableName = TABLE_NAME)
data class CharacterProfile(
    /*
    ID of this profile.
    This is the primary key, but is also hand-picked by the user.
     */
    @PrimaryKey
    val id: String,

    /*
    A long description of this character. E.g. their likes, dislikes, comedy preference.
     */
    val description: String,

    /*
    Mapping from package name to a list of string aliases.
    Example of package names: "com.discord,com.whatsapp". See accessibility_service_config.xml
    The string aliases correspond to the names parsed from the chat context.
    See [DiscordChatParser] for examples of why the same person might have multiple aliases.
    */
    val aliases: Map<String, List<String>>
) {
    companion object {
        // ID of myself. This is special because it's always added to any chat context.
        const val MY_ID = "Me"
        const val TABLE_NAME = "character_profiles"
    }
}