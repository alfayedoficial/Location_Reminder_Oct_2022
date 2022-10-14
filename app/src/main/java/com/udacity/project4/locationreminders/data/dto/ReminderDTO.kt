package com.udacity.project4.locationreminders.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.udacity.project4.locationreminders.data.local.typeConverters.DateTypeConverter
import java.util.*

/**
 * Immutable model class for a Reminder. In order to compile with Room
 *
 * @param title         title of the reminder
 * @param description   description of the reminder
 * @param address      location name of the reminder
 * @param latitude      latitude of the reminder location
 * @param longitude     longitude of the reminder location
 * @param id          id of the reminder
 */

@Entity(tableName = "reminders_table")
data class ReminderDTO(
    @ColumnInfo(name = "title") var title: String? = null,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "location") var location: String? = null,
    @ColumnInfo(name = "latitude") var latitude: Double?    = null,
    @ColumnInfo(name = "longitude") var longitude: Double? = null,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "date") var date: Date = Date(),
    @PrimaryKey @ColumnInfo(name = "entry_id") var id: String = UUID.randomUUID().toString()
)


typealias Reminders = List<ReminderDTO>
typealias RemindersMutableList = MutableList<ReminderDTO>