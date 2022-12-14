package com.udacity.project4.core.common.app

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.alfayedoficial.kotlinutils.KUPreferences
import com.udacity.project4.locationreminders.data.local.AppDatabase
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.features.saveRemind.viewModel.SaveReminderViewModel
import com.udacity.project4.locationreminders.features.reminderList.viewModel.RemindersListViewModel
import com.udacity.project4.utils.AppPreferences.initAppPreferences
import com.google.firebase.FirebaseApp
import com.udacity.project4.authentication.view.LoginViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class MyApp : MultiDexApplication()  , KoinComponent{

    companion object{

        private var _appPreferences: KUPreferences? = null
        var appPreferences: KUPreferences
            get() = _appPreferences!!
            set(value) { _appPreferences = value }
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        FirebaseApp.initializeApp(this)
        _appPreferences = initAppPreferences(this.applicationContext)


        val viewModelModule = module {
            viewModel { SaveReminderViewModel(dataSourceReminder = get() ) }
        }

        val viewModelModule2 = module {
            viewModel { RemindersListViewModel(dataSourceReminder = get() ) }
        }

        val viewModelModule3 = module {
            viewModel { LoginViewModel() }
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

        startKoin{
            androidContext(this@MyApp)
            modules(listOf(repositoryModel , databaseModel , viewModelModule , viewModelModule2 , viewModelModule3))
        }

    }

}


