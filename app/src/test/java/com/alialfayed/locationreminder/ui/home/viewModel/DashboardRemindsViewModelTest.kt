package com.alialfayed.locationreminder.ui.home.viewModel

import android.os.Build
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alialfayed.locationreminder.MainCoroutineRule
import com.alialfayed.locationreminder.data.FakeDataSource
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.Reminders
import com.alialfayed.locationreminder.getOrAwaitValue
import com.alialfayed.locationreminder.ui.home.viewModel.DashboardRemindsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.MatcherAssert
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
class DashboardRemindsViewModelTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRuleTest = MainCoroutineRule()


    private var dashboardRemindsViewModelForTesting: DashboardRemindsViewModel? = null
    private var fakeDataSourceForTesting: FakeDataSource? = null
    private var remindersForTesting : Reminders? = null


    @Before
    fun setupViewModel() {
        fakeDataSourceForTesting = FakeDataSource()
        dashboardRemindsViewModelForTesting = DashboardRemindsViewModel(fakeDataSourceForTesting!!)
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

        dashboardRemindsViewModelForTesting = DashboardRemindsViewModel(fakeDataSourceForTesting!!)

        dashboardRemindsViewModelForTesting!!.getReminders()

        assertThat(
            dashboardRemindsViewModelForTesting!!.reminders.getOrAwaitValue(), (IsNot.not(emptyList()))
        )

        assertThat(
            dashboardRemindsViewModelForTesting!!.reminders.getOrAwaitValue().size, Is.`is`(remindersForTesting!!.size)
        )

    }


    @Test
    fun getReminders_shouldReturnError(){

        fakeDataSourceForTesting = FakeDataSource(null)

        dashboardRemindsViewModelForTesting = DashboardRemindsViewModel(fakeDataSourceForTesting!!)

        dashboardRemindsViewModelForTesting!!.getReminders()

        assertThat(dashboardRemindsViewModelForTesting!!.errorLiveData.getOrAwaitValue(), Is.`is`("Error  Can not get reminders"))
    }

    @Test
    fun getReminders_CheckOfRemindersTasksLoading() = runBlocking {

        mainCoroutineRuleTest.pauseDispatcher()

        dashboardRemindsViewModelForTesting!!.getReminders()

        assertThat(dashboardRemindsViewModelForTesting!!.showLoadingLiveData.getOrAwaitValue(), Is.`is`(true))

        mainCoroutineRuleTest.resumeDispatcher()

        assertThat(dashboardRemindsViewModelForTesting!!.showLoadingLiveData.getOrAwaitValue(), Is.`is`(false))

    }

    @After
    fun koinStopWork() {
        stopKoin()
    }

}