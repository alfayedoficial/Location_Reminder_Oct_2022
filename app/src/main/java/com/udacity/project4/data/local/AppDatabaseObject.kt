package com.udacity.project4.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.data.local.typeConverters.DateTypeConverter



@Database(entities = [ReminderDTO::class], version = 3, exportSchema = false )
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderLocationDao(): ReminderLocationDao
}