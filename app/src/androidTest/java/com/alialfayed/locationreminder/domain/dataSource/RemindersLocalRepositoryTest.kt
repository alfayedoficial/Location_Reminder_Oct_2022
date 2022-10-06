package com.alialfayed.locationreminder.domain.dataSource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.ResultDatabase
import com.alialfayed.locationreminder.data.local.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()
    private var appDatabaseTest: AppDatabase? = null
    private var remindersLocalRepositoryTest: RemindersLocalRepository? = null

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

        remindersLocalRepositoryTest = RemindersLocalRepository(appDatabaseTest!!)
    }

    @Test
    fun insertITem_and_getItems() = runBlocking {
        val reminder = reminderDTOForTesting

        remindersLocalRepositoryTest!!.insertITem(reminder)

        val result = remindersLocalRepositoryTest!!.getItems() as ResultDatabase.Success

        assert(result.data!![0].id == reminder.id)
        assert(result.data!![0].title == reminder.title)
        assert(result.data!![0].description == reminder.description)
        assert(result.data!![0].address == reminder.address)
        assert(result.data!![0].latitude == reminder.latitude)
        assert(result.data!![0].longitude == reminder.longitude)

    }

    @Test
    fun insertITem_and_getItems_and_deleteItems() = runBlocking {
        val reminder = reminderDTOForTesting

        remindersLocalRepositoryTest!!.insertITem(reminder)

        val result = remindersLocalRepositoryTest!!.getItems() as ResultDatabase.Success

        assert(result.data!![0].id == reminder.id)
        assert(result.data!![0].title == reminder.title)
        assert(result.data!![0].description == reminder.description)
        assert(result.data!![0].address == reminder.address)
        assert(result.data!![0].latitude == reminder.latitude)
        assert(result.data!![0].longitude == reminder.longitude)

        remindersLocalRepositoryTest!!.clearTable()

        val result2 = remindersLocalRepositoryTest!!.getItems() as ResultDatabase.Success

        assert(result2.data!!.isEmpty())
    }


    @After
    fun closeRemindersDatabase() {
        appDatabaseTest!!.close()
    }



}