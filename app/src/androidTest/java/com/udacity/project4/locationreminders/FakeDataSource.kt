package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Reminders
import com.udacity.project4.locationreminders.data.dto.RemindersMutableList
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var fakeList : RemindersMutableList? = mutableListOf()) :
    ReminderDataSource {

    private var statusError = false

    override suspend fun getReminders(): Result<Reminders> {
        return if (statusError) {
            Result.Error("Error  Can not get reminders")
        } else {
            if (fakeList.isNullOrEmpty()) {
                Result.Error("Error  Can not get reminders")
            } else {
                Result.Success(fakeList)
            }
        }
    }

    override suspend fun saveReminder(item: ReminderDTO) {
        fakeList?.add(item)
    }

    override suspend fun getReminderById(id: String): Result<ReminderDTO?> {
        return if (statusError) {
            Result.Error("Error  Can not get reminder")
        } else {
            if (fakeList.isNullOrEmpty()) {
                Result.Error("Error  Can not get reminder")
            } else {
                Result.Success(fakeList?.find { it.id == id })
            }
        }
    }

    override suspend fun deleteAllReminders() {
        fakeList?.clear()
    }

    fun setCheckReturnError(statusError :Boolean) {
        this.statusError = statusError
    }


}