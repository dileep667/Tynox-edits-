package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

class TimelineConverters {

    @TypeConverter
    fun fromAspectRatio(value: AspectRatio): String = value.name

    @TypeConverter
    fun toAspectRatio(value: String): AspectRatio = try {
        AspectRatio.valueOf(value)
    } catch (e: Exception) {
        AspectRatio.RATIO_9_16
    }

    @TypeConverter
    fun fromResolution(value: ProjectResolution): String = value.name

    @TypeConverter
    fun toResolution(value: String): ProjectResolution = try {
        ProjectResolution.valueOf(value)
    } catch (e: Exception) {
        ProjectResolution.RES_1080P
    }

    @TypeConverter
    fun fromFps(value: ProjectFps): String = value.name

    @TypeConverter
    fun toFps(value: String): ProjectFps = try {
        ProjectFps.valueOf(value)
    } catch (e: Exception) {
        ProjectFps.FPS_30
    }
}
