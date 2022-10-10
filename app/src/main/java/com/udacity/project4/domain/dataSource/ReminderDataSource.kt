package com.udacity.project4.domain.dataSource

import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.data.dto.Reminders
import com.udacity.project4.data.dto.ResultDatabase


interface ReminderDataSource {

    suspend fun getItems(): ResultDatabase<Reminders>
    suspend fun insertITem(item: ReminderDTO)
    suspend fun getItemById(id: String): ResultDatabase<ReminderDTO?>
    suspend fun clearTable()
}