package com.udacity.project4.data.local.typeConverters

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun fromDateToString(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter
    fun fromStringToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
}