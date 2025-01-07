package com.comedy.suggester.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * So RoomDB can store Map<String, List<String>>
 */
class MapTypeConverter {
    @TypeConverter
    fun fromString(value: String): Map<String, List<String>> {
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, List<String>>): String {
        return Gson().toJson(map)
    }
}