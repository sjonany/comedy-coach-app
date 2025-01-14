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
    The character's sense of humor.
    In the past, this used to be character's background, but that made the jokes over-fixate on
    the same themes like the character's jobs. Someday I might add the character background back,
    but for now we'll leave the relevant character background for the user to add in the user hint.

    Here's an example that worked best for me:
    "Here are some patterns that I like to use
    - Sexual/crude humor
    - Often uses irony and wordplay
    - Self-deprecating
    - Sarcastic/playful mockery, especially with friends
    - Quick-witted responses
    - Mixing casual and formal language for comedic effect
    - Topical/social commentary
    - Employs cultural references and memes
    - Competitive banter/playful arrogance
    - Includes religious humor/references as contrast"
     */
    val senseOfHumor: String,


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