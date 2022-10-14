package com.udacity.project4.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.entity.ReminderDataItem
import com.udacity.project4.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.locationreminders.features.saveRemind.viewModel.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P)
class SaveReminderViewModelTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRuleTest = MainCoroutineRule()


    private var saveReminderViewModelForTesting: SaveReminderViewModel? = null
    private var fakeDataSourceForTesting: FakeDataSource? = null

    private var reminderDataItemForTesting: ReminderDataItem = ReminderDataItem().apply {
        this.id = 1.toString()
        this.title = "title"
        this.description = "description"
        this.location = "location"
        this.latitude = 47.5456551
        this.longitude = 122.0101731
    }


    @Before
    fun setupViewModel() {
        fakeDataSourceForTesting = FakeDataSource()
        saveReminderViewModelForTesting = SaveReminderViewModel(fakeDataSourceForTesting!!)

        runBlocking { fakeDataSourceForTesting!!.deleteAllReminders() }
    }

    @Test
    fun saveReminderEntityForTesting() {

        saveReminderViewModelForTesting!!.reminderDataItem.postValue(reminderDataItemForTesting)
        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.reminderDataItem.value, CoreMatchers.notNullValue())
    }

    @Test
    fun `getSaveRemindState with all data`() {

        saveReminderViewModelForTesting!!.title.postValue(reminderDataItemForTesting.title)
        saveReminderViewModelForTesting!!.description.postValue(reminderDataItemForTesting.description)
        saveReminderViewModelForTesting!!.address.postValue(reminderDataItemForTesting.location)
        saveReminderViewModelForTesting!!.location.postValue(LatLng(reminderDataItemForTesting.latitude!!, reminderDataItemForTesting.longitude!!))

        saveReminderViewModelForTesting!!.saveReminderEntity()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.reminderDataItem.getOrAwaitValue(), CoreMatchers.notNullValue())
    }


    @Test
    fun `getSaveRemindState with empty title`() {

        saveReminderViewModelForTesting!!.title.postValue("")
        saveReminderViewModelForTesting!!.description.postValue(reminderDataItemForTesting.description)
        saveReminderViewModelForTesting!!.address.postValue(reminderDataItemForTesting.location)
        saveReminderViewModelForTesting!!.location.postValue(LatLng(reminderDataItemForTesting.latitude!!, reminderDataItemForTesting.longitude!!))

        saveReminderViewModelForTesting!!.saveReminderEntity()
        saveReminderViewModelForTesting!!.validateEnteredData()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.errorState.getOrAwaitValue(), CoreMatchers.notNullValue())
    }

    @Test
    fun `saveReminderToDatabase and showLocationLoading`() = runBlocking {

        saveReminderViewModelForTesting!!.reminderDataItem.postValue(reminderDataItemForTesting)

        mainCoroutineRuleTest.pauseDispatcher()

        saveReminderViewModelForTesting!!.saveReminderToDatabase()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.loadingState.getOrAwaitValue(), CoreMatchers.`is`(true))

        mainCoroutineRuleTest.resumeDispatcher()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.loadingState.getOrAwaitValue(), CoreMatchers.`is`(false))

    }
}