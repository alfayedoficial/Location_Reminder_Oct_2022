package com.alialfayed.locationreminder.ui.home.features.dashboardReminds.view

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.alialfayed.locationreminder.data.FakeDataSource
import com.alialfayed.locationreminder.domain.dataSource.ReminderDataSource
import com.alialfayed.locationreminder.ui.home.viewModel.DashboardRemindsViewModel
import com.alialfayed.locationreminder.util.DataBindingIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.util.monitorFragment
import org.hamcrest.CoreMatchers

import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class DashboardRemindsFragmentTest {

    @get:Rule
    var instantTaskExecutorRuleTest = InstantTaskExecutorRule()
    private lateinit var fakeLocalDataSourceForTesting: ReminderDataSource
    private lateinit var dashboardRemindsViewModelTest: DashboardRemindsViewModel
    private val binding = DataBindingIdlingResource()

    private var reminderDTOForTesting: ReminderDTO = ReminderDTO().apply {
        this.id = 1.toString()
        this.title = "title"
        this.description = "description"
        this.address = "location"
        this.latitude = 47.5456551
        this.longitude = 122.0101731
    }

    @Before
    fun setupTest() {
        fakeLocalDataSourceForTesting = FakeDataSource()
        dashboardRemindsViewModelTest = DashboardRemindsViewModel(fakeLocalDataSourceForTesting)
        stopKoin()
        modules()
    }

    private fun modules() {
        val myModule = module {
            single {
                dashboardRemindsViewModelTest
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun startNavTest() = runBlockingTest {
        val fragment = launchFragmentInContainer<DashboardRemindsFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        fragment.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(R.id.action_DashboardRemindsFragment_to_saveRemindFragment)
    }


    @Test
    fun display_and_notDisplayUi() = runBlockingTest {

        fakeLocalDataSourceForTesting.insertITem(reminderDTOForTesting)
        launchFragmentInContainer<DashboardRemindsFragment>(Bundle.EMPTY, R.style.AppTheme)

        Espresso.onView(withId(R.id.tvNoRemindersIndicator))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(withText(reminderDTOForTesting.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminderDTOForTesting.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(reminderDTOForTesting.address))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }


    @Test
    fun displayError()  {
        val fragmentScenario = launchFragmentInContainer<DashboardRemindsFragment>(Bundle.EMPTY, R.style.AppTheme)
        binding.monitorFragment(fragmentScenario)
        val controller = Mockito.mock(NavController::class.java)

        fragmentScenario.onFragment {
            Navigation.setViewNavController(it.view!!, controller)
        }

        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())
        Mockito.verify(controller).navigate(R.id.action_DashboardRemindsFragment_to_saveRemindFragment)
    }
}



