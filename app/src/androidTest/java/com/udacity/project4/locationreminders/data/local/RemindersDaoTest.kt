package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()

    private var appDatabaseTest: AppDatabase? = null

    private var reminderDTOForTesting: ReminderDTO = ReminderDTO().apply {
        this.id = 1.toString()
        this.title = "title"
        this.description = "description"
        this.location = "location"
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
    fun insertItem_and_getItem() = runBlockingTest {
        val reminder =  reminderDTOForTesting

        appDatabaseTest?.reminderLocationDao()?.saveReminder(reminder)

        val reminderFromDB = appDatabaseTest?.reminderLocationDao()?.getReminders()!![0]

        assert(reminderFromDB.id == reminder.id)
        assert(reminderFromDB.title == reminder.title)
        assert(reminderFromDB.description == reminder.description)
        assert(reminderFromDB.location == reminder.location)
        assert(reminderFromDB.latitude == reminder.latitude)
        assert(reminderFromDB.longitude == reminder.longitude)
    }

    @Test
    fun insertItem_and_getItemById()= runBlockingTest {

        val reminder =  reminderDTOForTesting

        appDatabaseTest?.reminderLocationDao()?.saveReminder(reminder)

        val reminderFromDB = appDatabaseTest?.reminderLocationDao()?.getReminderById(reminder.id)!!

        assert(reminderFromDB.id == reminder.id)
        assert(reminderFromDB.title == reminder.title)
        assert(reminderFromDB.description == reminder.description)
        assert(reminderFromDB.location == reminder.location)
        assert(reminderFromDB.latitude == reminder.latitude)
        assert(reminderFromDB.longitude == reminder.longitude)

    }

    @Test
    fun insertItem_and_deleteItem() = runBlockingTest {
        val reminder =  reminderDTOForTesting

        appDatabaseTest?.reminderLocationDao()?.saveReminder(reminder)

        appDatabaseTest?.reminderLocationDao()?.deleteAllReminders()

        val reminderFromDB = appDatabaseTest?.reminderLocationDao()?.getReminders()

        assert(reminderFromDB?.size == 0)
    }

    @Test
    fun insetItem_and_getItemById_and_getError() = runBlockingTest {
        val reminder =  reminderDTOForTesting

        appDatabaseTest?.reminderLocationDao()?.saveReminder(reminder)

        val reminderFromDB = appDatabaseTest?.reminderLocationDao()?.getReminderById("2")

        assert(reminderFromDB == null)
    }

    @After
    fun closeRemindersDatabase() {
        appDatabaseTest!!.close()
    }

}