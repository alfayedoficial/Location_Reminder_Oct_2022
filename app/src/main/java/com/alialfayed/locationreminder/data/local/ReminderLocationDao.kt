package com.alialfayed.locationreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.Reminders

@Dao
interface ReminderLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: ReminderDTO)

    @Query("SELECT * FROM reminders_table where entry_id = :reminderId ORDER BY date ASC")
    fun getItemById(reminderId: String): ReminderDTO?

    @Query("SELECT * FROM reminders_table")
    fun getItems():Reminders

    @Query("DELETE FROM reminders_table")
    fun clearTable()
}