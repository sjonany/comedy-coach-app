package com.comedy.controller.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.comedy.controller.data.AppSetting.Companion.TABLE_NAME

// Data model for all settings of this app that's persisted.
@Entity(tableName = TABLE_NAME)
data class AppSetting(
    /*
    ID of this setting.
    Currently there should be just one with a hardcoded value: "DEFAULT"
     */
    @PrimaryKey
    val id: String = DEFAULT_ID,

    /*
    API key used to interact w/ Open AI API.
    Go here: https://platform.openai.com/settings/organization/api-keys
    */
    val openAiApiKey: String
) {
    companion object {
        // ID of the default setting. For now there should just be one setting row and it has this ID.
        const val DEFAULT_ID = "DEFAULT"
        const val TABLE_NAME = "app_settings"
    }
}