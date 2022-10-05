package com.alialfayed.locationreminder.domain.dataSource

import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.Reminders
import com.alialfayed.locationreminder.data.dto.ResultDatabase
import kotlinx.coroutines.flow.Flow


interface ReminderDataSource {

    suspend fun getItems(): ResultDatabase<Reminders>
    suspend fun insertITem(item: ReminderDTO)
    suspend fun getItemById(id: String): ResultDatabase<ReminderDTO?>
    suspend fun clearTable()
}