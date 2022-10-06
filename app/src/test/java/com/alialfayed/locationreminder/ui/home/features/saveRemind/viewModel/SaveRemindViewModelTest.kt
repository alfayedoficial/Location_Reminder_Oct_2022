package com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alialfayed.locationreminder.MainCoroutineRule
import com.alialfayed.locationreminder.data.FakeDataSource
import com.alialfayed.locationreminder.data.dto.Reminders
import com.alialfayed.locationreminder.domain.entity.ReminderEntity
import com.alialfayed.locationreminder.getOrAwaitValue
import com.alialfayed.locationreminder.ui.home.viewModel.DashboardRemindsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P)
class SaveRemindViewModelTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRuleTest = MainCoroutineRule()


    private var saveRemindViewModelForTesting: SaveRemindViewModel? = null
    private var fakeDataSourceForTesting: FakeDataSource? = null

    private var reminderEntityForTesting: ReminderEntity = ReminderEntity().apply {
        this.id = 1.toString()
        this.title = "title"
        this.description = "description"
        this.address = "location"
        this.location = LatLng(47.5456551,122.0101731)
    }


    @Before
    fun setupViewModel() {
        fakeDataSourceForTesting = FakeDataSource()
        saveRemindViewModelForTesting = SaveRemindViewModel(fakeDataSourceForTesting!!)

        runBlocking { fakeDataSourceForTesting!!.clearTable() }
    }

    @Test
    fun saveReminderEntityForTesting() {

        saveRemindViewModelForTesting!!.reminderEntity.postValue(reminderEntityForTesting)
        MatcherAssert.assertThat(saveRemindViewModelForTesting!!.reminderEntity.value, CoreMatchers.notNullValue())
    }

    @Test
    fun `getSaveRemindState with all data`() {

        saveRemindViewModelForTesting!!.title.postValue(reminderEntityForTesting.title)
        saveRemindViewModelForTesting!!.description.postValue(reminderEntityForTesting.description)
        saveRemindViewModelForTesting!!.address.postValue(reminderEntityForTesting.address)
        saveRemindViewModelForTesting!!.location.postValue(reminderEntityForTesting.location)

        saveRemindViewModelForTesting!!.saveReminderEntity()

        MatcherAssert.assertThat(saveRemindViewModelForTesting!!.reminderEntity.getOrAwaitValue(), CoreMatchers.notNullValue())
    }


    @Test
    fun `getSaveRemindState with empty title`() {

        saveRemindViewModelForTesting!!.title.postValue("")
        saveRemindViewModelForTesting!!.description.postValue(reminderEntityForTesting.description)
        saveRemindViewModelForTesting!!.address.postValue(reminderEntityForTesting.address)
        saveRemindViewModelForTesting!!.location.postValue(reminderEntityForTesting.location)

        saveRemindViewModelForTesting!!.saveReminderEntity()
        saveRemindViewModelForTesting!!.validateEnteredData()

        MatcherAssert.assertThat(saveRemindViewModelForTesting!!.errorState.getOrAwaitValue(), CoreMatchers.notNullValue())
    }

    @Test
    fun `saveReminderToDatabase and showLocationLoading`() = runBlocking {

        saveRemindViewModelForTesting!!.reminderEntity.postValue(reminderEntityForTesting)

        mainCoroutineRuleTest.pauseDispatcher()

        saveRemindViewModelForTesting!!.saveReminderToDatabase()

        MatcherAssert.assertThat(saveRemindViewModelForTesting!!.loadingState.getOrAwaitValue(), CoreMatchers.`is`(true))

        mainCoroutineRuleTest.resumeDispatcher()

        MatcherAssert.assertThat(saveRemindViewModelForTesting!!.loadingState.getOrAwaitValue(), CoreMatchers.`is`(false))

    }
}