package com.alialfayed.locationreminder.data

import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.Reminders
import com.alialfayed.locationreminder.data.dto.RemindersMutableList
import com.alialfayed.locationreminder.data.dto.ResultDatabase
import com.alialfayed.locationreminder.domain.dataSource.ReminderDataSource

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var fakeList : RemindersMutableList? = mutableListOf()) : ReminderDataSource {

    private var statusError = false

    override suspend fun getItems(): ResultDatabase<Reminders> {
        return if (statusError) {
            ResultDatabase.Error("Error  Can not get reminders")
        } else {
            if (fakeList.isNullOrEmpty()) {
                ResultDatabase.Error("Error  Can not get reminders")
            } else {
                ResultDatabase.Success(fakeList)
            }
        }
    }

    override suspend fun insertITem(item: ReminderDTO) {
        fakeList?.add(item)
    }

    override suspend fun getItemById(id: String): ResultDatabase<ReminderDTO?> {
        return if (statusError) {
            ResultDatabase.Error("Error  Can not get reminder")
        } else {
            if (fakeList.isNullOrEmpty()) {
                ResultDatabase.Error("Error  Can not get reminder")
            } else {
                ResultDatabase.Success(fakeList?.find { it.id == id })
            }
        }
    }

    override suspend fun clearTable() {
        fakeList?.clear()
    }

    fun setCheckReturnError(statusError :Boolean) {
        this.statusError = statusError
    }


}