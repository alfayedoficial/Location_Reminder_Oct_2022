package com.udacity.project4.ui.home.view

import android.app.Activity
import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.data.local.AppDatabase
import com.udacity.project4.domain.dataSource.ReminderDataSource
import com.udacity.project4.domain.dataSource.RemindersLocalRepository
import com.udacity.project4.ui.home.features.saveRemind.viewModel.SaveReminderViewModel
import com.udacity.project4.ui.home.viewModel.DashboardRemindsViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import com.udacity.project4.R

@RunWith(AndroidJUnit4::class)
@LargeTest
class OneSingleActivityTest() : KoinTest {

    private lateinit var dataSourceForTest: ReminderDataSource
    private lateinit var contextForTest: Application
    private lateinit var activityForTest: OneSingleActivity
    private lateinit var saveReminderViewModelTest: SaveReminderViewModel
    private val binding = DataBindingIdlingResource()

    @get:Rule
    var oneSingleActivityTestRule: ActivityTestRule<OneSingleActivity> = ActivityTestRule(OneSingleActivity::class.java)


    private fun getActivity(activityScenario: ActivityScenario<OneSingleActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    @Before
    fun setup() {
        stopKoin()//stop the original app koin
        activityForTest = ApplicationProvider.getApplicationContext()


        val viewModelModule = module {
            viewModel { SaveReminderViewModel(dataSourceReminder = get() ) }
        }

        val viewModelModule2 = module {
            viewModel { DashboardRemindsViewModel(dataSourceReminder = get() ) }
        }

        val repositoryModel: Module = module {
            single<ReminderDataSource> { RemindersLocalRepository(appDatabase = get()) }
        }

        val databaseModel: Module = module {
            fun getDatabaseInstance(application: Application): AppDatabase {
                return Room.databaseBuilder(application, AppDatabase::class.java, "user_database"
                ).fallbackToDestructiveMigration().build()
            }
            single { getDatabaseInstance(androidApplication()) }
        }

        //declare a new koin module
        GlobalContext.startKoin {
            modules(listOf(repositoryModel, databaseModel, viewModelModule, viewModelModule2))
        }
        //Get our real repository
        activityForTest = oneSingleActivityTestRule.activity
        dataSourceForTest = get()
        //clear the data to start fresh
        runBlocking {
            dataSourceForTest.clearTable()
        }
        saveReminderViewModelTest = GlobalContext.get().get()
    }



    @Before
    fun regIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(binding)
    }

    @After
    fun unRegIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(binding)
    }

    @Test
    fun saveReminder_and_displayMessage() = runBlockingTest {

        val activityScenario = ActivityScenario.launch(OneSingleActivity::class.java)
        binding.monitorActivity(activityScenario)

        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.remindTitleEt)).perform(ViewActions.typeText("Title"))
        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.remindDescriptionEt)).perform(ViewActions.typeText("Description"))
        ViewActions.closeSoftKeyboard()

        Espresso.onView(withId(R.id.cardViewAddress)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.map)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.materialButton)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.btnSaveRemind)).perform(ViewActions.click())

        Espresso.onView(withText(R.string.reminder_saved)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    getActivity(
                        activityScenario
                    )!!.window.decorView
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun saveReminder_and_displaySnackBar() = runBlockingTest {
        val activityScenario = ActivityScenario.launch(OneSingleActivity::class.java)
        binding.monitorActivity(activityScenario)

        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.remindTitleEt)).perform(ViewActions.typeText("Title"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.btnSaveRemind)).perform(ViewActions.click())

        Espresso.onView(
            CoreMatchers.allOf(
                withId(com.google.android.material.R.id.snackbar_text),
                withText(activityForTest.getString(R.string.err_select_location))
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}