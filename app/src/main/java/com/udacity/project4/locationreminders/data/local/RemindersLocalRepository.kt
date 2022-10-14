package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Reminders
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.ReminderDataSource
import kotlinx.coroutines.*


class RemindersLocalRepository(private val appDatabase: AppDatabase) : ReminderDataSource {

    override suspend fun getReminders(): Result<Reminders> = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        return@withContext try {
            Result.Success(data = appDatabase.reminderLocationDao().getReminders())
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(item: ReminderDTO)  = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        appDatabase.reminderLocationDao().saveReminder(item)
    }

    override suspend fun getReminderById(id: String): Result<ReminderDTO?> = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        return@withContext try {
            Result.Success(data = appDatabase.reminderLocationDao().getReminderById(id))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        appDatabase.reminderLocationDao().deleteAllReminders()
    }


}
