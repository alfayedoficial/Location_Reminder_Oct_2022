package com.alialfayed.locationreminder.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.domain.entity.ReminderEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRuleTest = InstantTaskExecutorRule()

    private var appDatabaseTest: AppDatabase? = null

    private var reminderDTOForTesting: ReminderDTO = ReminderDTO().apply {
        this.id = 1.toString()
        this.title = "title"
        this.description = "description"
        this.address = "location"
        this.latitude = 47.5456551
        this.longitude = 122.0101731
    }

    @Before
    fun setup() {
        appDatabaseTest = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertItem_and_getItem() {
        val reminder =  reminderDTOForTesting

        appDatabaseTest?.reminderLocationDao()?.insertItem(reminder)

        val reminderFromDB = appDatabaseTest?.reminderLocationDao()?.getItems()!![0]

        assert(reminderFromDB.id == reminder.id)
        assert(reminderFromDB.title == reminder.title)
        assert(reminderFromDB.description == reminder.description)
        assert(reminderFromDB.address == reminder.address)
        assert(reminderFromDB.latitude == reminder.latitude)
        assert(reminderFromDB.longitude == reminder.longitude)
    }



}