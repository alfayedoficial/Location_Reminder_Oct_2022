package com.alialfayed.locationreminder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.local.typeConverters.DateTypeConverter



@Database(entities = [ReminderDTO::class], version = 3, exportSchema = false )
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderLocationDao(): ReminderLocationDao
}