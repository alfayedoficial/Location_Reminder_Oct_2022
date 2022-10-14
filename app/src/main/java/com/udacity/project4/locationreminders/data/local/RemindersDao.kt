package com.udacity.project4.locationreminders.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Reminders

@Dao
interface RemindersDao {

    /**
     * Insert a reminder in the database. If the reminder already exists, replace it.
     *
     * @param reminder the reminder to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReminder(reminder: ReminderDTO)

    /**
     * @param reminderId the id of the reminder
     * @return the reminder object with the reminderId
     */
    @Query("SELECT * FROM reminders_table where entry_id = :reminderId ORDER BY date ASC")
    fun getReminderById(reminderId: String): ReminderDTO?

    /**
     * @return all reminders.
     */
    @Query("SELECT * FROM reminders_table")
    fun getReminders(): Reminders

    /**
     * Delete all reminders.
     */
    @Query("DELETE FROM reminders_table")
    fun deleteAllReminders()
}