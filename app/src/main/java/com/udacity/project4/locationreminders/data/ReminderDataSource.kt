package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Reminders
import com.udacity.project4.locationreminders.data.dto.Result


interface ReminderDataSource {

    suspend fun getReminders(): Result<Reminders>
    suspend fun saveReminder(item: ReminderDTO)
    suspend fun getReminderById(id: String): Result<ReminderDTO?>
    suspend fun deleteAllReminders()
}