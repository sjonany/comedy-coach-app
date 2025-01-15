package com.comedy.suggester.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MyTypeConverters {

    /**
     * So RoomDB can store Map<String, List<String>>
     */
    @TypeConverter
    fun fromStringToMap(value: String): Map<String, List<String>> {
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, List<String>>): String {
        return Gson().toJson(map)
    }

    /**
     * For LlmModel enum
     */
    @TypeConverter
    fun fromStringToLlmModel(value: String): LlmModel {
        return LlmModel.valueOf(value)
    }

    @TypeConverter
    fun fromLlmModel(value: LlmModel): String {
        return value.name
    }
}