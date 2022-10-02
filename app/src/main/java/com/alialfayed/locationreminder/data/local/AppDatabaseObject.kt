package com.alialfayed.locationreminder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alialfayed.locationreminder.data.dto.ReminderDTO


fun createRoomDatabase(context: Context): RoomDatabase {
    return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "locationReminders.db")
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
        .build()
}

@Database(entities = [ReminderDTO::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderLocationDao(): ReminderLocationDao
}