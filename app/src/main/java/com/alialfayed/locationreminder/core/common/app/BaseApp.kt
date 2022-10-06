package com.alialfayed.locationreminder.core.common.app

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.alfayedoficial.kotlinutils.KUPreferences
import com.alialfayed.locationreminder.data.local.AppDatabase
import com.alialfayed.locationreminder.domain.dataSource.ReminderDataSource
import com.alialfayed.locationreminder.domain.dataSource.RemindersLocalRepository
import com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel.SaveReminderViewModel
import com.alialfayed.locationreminder.ui.home.viewModel.DashboardRemindsViewModel
import com.alialfayed.locationreminder.utils.AppPreferences.initAppPreferences
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class BaseApp : MultiDexApplication()  , KoinComponent{

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

        startKoin{
            androidContext(this@BaseApp)
            modules(listOf(repositoryModel , databaseModel , viewModelModule , viewModelModule2))
        }

    }

}


