package com.udacity.project4.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Reminders
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.features.reminderList.viewModel.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRuleTest = MainCoroutineRule()


    private var remindersListViewModelForTesting: RemindersListViewModel? = null
    private var fakeDataSourceForTesting: FakeDataSource? = null
    private var remindersForTesting : Reminders? = null


    @Before
    fun setupViewModel() {
        fakeDataSourceForTesting = FakeDataSource()
        remindersListViewModelForTesting = RemindersListViewModel(fakeDataSourceForTesting!!)
    }

    @Test
    fun getReminders_HasValueTesting() {
        remindersForTesting = listOf(
            ReminderDTO("Task NO.1", "description 1", "City ", 30.043457431, 31.2765762),
            ReminderDTO("Task NO.2", "description 2", "City ", 30.043457431, 31.2765762),
            ReminderDTO("Task NO.3", "description 3", "City ", 30.043457431, 31.2765762),
            ReminderDTO("Task NO.4", "description 4", "City ", 30.043457431, 31.2765762),
            ReminderDTO("Task NO.5", "description 5", "City ", 30.043457431, 31.2765762),
            ReminderDTO("Task NO.6", "description 6", "City ", 30.043457431, 31.2765762)
        )

        fakeDataSourceForTesting = FakeDataSource(remindersForTesting!!.toMutableList())

        remindersListViewModelForTesting = RemindersListViewModel(fakeDataSourceForTesting!!)

        remindersListViewModelForTesting!!.getReminders()

        assertThat(
            remindersListViewModelForTesting!!.reminders.getOrAwaitValue(), (IsNot.not(emptyList()))
        )

        assertThat(
            remindersListViewModelForTesting!!.reminders.getOrAwaitValue().size, Is.`is`(remindersForTesting!!.size)
        )

    }


    @Test
    fun getReminders_shouldReturnError(){

        fakeDataSourceForTesting = FakeDataSource(null)

        remindersListViewModelForTesting = RemindersListViewModel(fakeDataSourceForTesting!!)

        remindersListViewModelForTesting!!.getReminders()

        assertThat(remindersListViewModelForTesting!!.errorLiveData.getOrAwaitValue(), Is.`is`("Error  Can not get reminders"))
    }

    @Test
    fun getReminders_CheckOfRemindersTasksLoading() = runBlocking {

        mainCoroutineRuleTest.pauseDispatcher()

        remindersListViewModelForTesting!!.getReminders()

        assertThat(remindersListViewModelForTesting!!.showLoadingLiveData.getOrAwaitValue(), Is.`is`(true))

        mainCoroutineRuleTest.resumeDispatcher()

        assertThat(remindersListViewModelForTesting!!.showLoadingLiveData.getOrAwaitValue(), Is.`is`(false))

    }

    @After
    fun koinStopWork() {
        stopKoin()
    }

}