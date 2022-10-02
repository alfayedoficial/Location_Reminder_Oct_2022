package com.alialfayed.locationreminder.core.common.app

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alfayedoficial.kotlinutils.KUPreferences
import com.alialfayed.locationreminder.data.local.createRoomDatabase
import com.alialfayed.locationreminder.domain.usecase.ReminderDataSource
import com.alialfayed.locationreminder.domain.usecase.RemindersLocalRepository
import com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel.SaveRemindViewModel
import com.alialfayed.locationreminder.ui.home.viewModel.OneSingleViewModel
import com.alialfayed.locationreminder.utils.AppPreferences.initAppPreferences
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class BaseApp : MultiDexApplication() {


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

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                OneSingleViewModel()
            }

//            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveRemindViewModel(get())
            }

            single { createRoomDatabase(this@BaseApp) }
            single { RemindersLocalRepository(get()) }
            singleOf(::RemindersLocalRepository){ bind<ReminderDataSource>() }


        }

        startKoin {
            androidContext(this@BaseApp)
            modules(listOf(myModule))
        }
    }

}

