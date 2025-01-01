package com.comedy.suggester.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.comedy.suggester.data.AppSettings.Companion.TABLE_NAME

/**
 * Data model for all settings of this app that's persisted.
 */
@Entity(tableName = TABLE_NAME)
data class AppSettings(
    /*
    ID of this setting.
    Currently there should be just one with a hardcoded value: "MAIN"
     */
    @PrimaryKey
    val id: String = MAIN_ID,

    /*
    API key used to interact w/ Open AI API.
    Go here: https://platform.openai.com/settings/organization/api-keys
    */
    val openAiApiKey: String
) {
    companion object {
        // ID of the main settings. For now there should just be one settings row and it has this ID.
        const val MAIN_ID = "MAIN"
        const val TABLE_NAME = "app_settings"
    }
}