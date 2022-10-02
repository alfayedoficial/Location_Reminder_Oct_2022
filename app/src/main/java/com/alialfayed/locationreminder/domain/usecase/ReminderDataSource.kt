package com.alialfayed.locationreminder.domain.usecase

import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.ResultDatabase


interface ReminderDataSource {

    suspend fun getItems(): ResultDatabase<List<ReminderDTO>>
    suspend fun insertITem(item: ReminderDTO)
    suspend fun getItemById(id: String): ResultDatabase<ReminderDTO?>
    suspend fun clearTable()
}