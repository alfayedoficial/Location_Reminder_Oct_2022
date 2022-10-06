package com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alialfayed.locationreminder.MainCoroutineRule
import com.alialfayed.locationreminder.data.FakeDataSource
import com.alialfayed.locationreminder.domain.entity.ReminderEntity
import com.alialfayed.locationreminder.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
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
        saveReminderViewModelForTesting = SaveReminderViewModel(fakeDataSourceForTesting!!)

        runBlocking { fakeDataSourceForTesting!!.clearTable() }
    }

    @Test
    fun saveReminderEntityForTesting() {

        saveReminderViewModelForTesting!!.reminderEntity.postValue(reminderEntityForTesting)
        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.reminderEntity.value, CoreMatchers.notNullValue())
    }

    @Test
    fun `getSaveRemindState with all data`() {

        saveReminderViewModelForTesting!!.title.postValue(reminderEntityForTesting.title)
        saveReminderViewModelForTesting!!.description.postValue(reminderEntityForTesting.description)
        saveReminderViewModelForTesting!!.address.postValue(reminderEntityForTesting.address)
        saveReminderViewModelForTesting!!.location.postValue(reminderEntityForTesting.location)

        saveReminderViewModelForTesting!!.saveReminderEntity()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.reminderEntity.getOrAwaitValue(), CoreMatchers.notNullValue())
    }


    @Test
    fun `getSaveRemindState with empty title`() {

        saveReminderViewModelForTesting!!.title.postValue("")
        saveReminderViewModelForTesting!!.description.postValue(reminderEntityForTesting.description)
        saveReminderViewModelForTesting!!.address.postValue(reminderEntityForTesting.address)
        saveReminderViewModelForTesting!!.location.postValue(reminderEntityForTesting.location)

        saveReminderViewModelForTesting!!.saveReminderEntity()
        saveReminderViewModelForTesting!!.validateEnteredData()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.errorState.getOrAwaitValue(), CoreMatchers.notNullValue())
    }

    @Test
    fun `saveReminderToDatabase and showLocationLoading`() = runBlocking {

        saveReminderViewModelForTesting!!.reminderEntity.postValue(reminderEntityForTesting)

        mainCoroutineRuleTest.pauseDispatcher()

        saveReminderViewModelForTesting!!.saveReminderToDatabase()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.loadingState.getOrAwaitValue(), CoreMatchers.`is`(true))

        mainCoroutineRuleTest.resumeDispatcher()

        MatcherAssert.assertThat(saveReminderViewModelForTesting!!.loadingState.getOrAwaitValue(), CoreMatchers.`is`(false))

    }
}