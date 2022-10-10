package com.udacity.project4.domain.dataSource

import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.data.dto.Reminders
import com.udacity.project4.data.dto.ResultDatabase
import com.udacity.project4.data.local.AppDatabase
import kotlinx.coroutines.*


class RemindersLocalRepository(private val appDatabase: AppDatabase) : ReminderDataSource {

    override suspend fun getItems(): ResultDatabase<Reminders> = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        return@withContext try {
            ResultDatabase.Success(data = appDatabase.reminderLocationDao().getItems())
        } catch (ex: Exception) {
            ResultDatabase.Error(ex.localizedMessage)
        }
    }

    override suspend fun insertITem(item: ReminderDTO)  = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        appDatabase.reminderLocationDao().insertItem(item)
    }

    override suspend fun getItemById(id: String): ResultDatabase<ReminderDTO?> = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        return@withContext try {
            ResultDatabase.Success(data = appDatabase.reminderLocationDao().getItemById(id))
        } catch (ex: Exception) {
            ResultDatabase.Error(ex.localizedMessage)
        }
    }

    override suspend fun clearTable() = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        appDatabase.reminderLocationDao().clearTable()
    }


}
