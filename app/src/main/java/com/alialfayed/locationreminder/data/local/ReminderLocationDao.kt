package com.alialfayed.locationreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alialfayed.locationreminder.data.dto.ReminderDTO

@Dao
interface ReminderLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: ReminderDTO)

    @Query("SELECT * FROM reminders_table where entry_id = :reminderId")
    fun getItemById(reminderId: String): ReminderDTO?

    @Query("SELECT * FROM reminders_table")
    fun getItems():List<ReminderDTO>

    @Query("DELETE FROM reminders_table")
    fun clearTable()
}