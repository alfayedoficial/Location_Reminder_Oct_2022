package com.alialfayed.locationreminder.ui.home.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alialfayed.locationreminder.data.dto.Reminders
import com.alialfayed.locationreminder.data.dto.ResultDatabase
import com.alialfayed.locationreminder.domain.dataSource.ReminderDataSource
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.launch

class DashboardRemindsViewModel(private val dataSourceReminder: ReminderDataSource) : ViewModel() {


    private val _reminders = MutableLiveData<Reminders>(arrayListOf())
    val reminders: LiveData<Reminders> = _reminders


    private val _errorMutableLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorMutableLiveData

    private val _showLoadingMutableLiveData = MutableLiveData<Boolean>()
    val showLoadingLiveData: LiveData<Boolean> = _showLoadingMutableLiveData

    var status = false

    private val _logoutState = MutableLiveData<Boolean?>()
    val logoutState: LiveData<Boolean?> = _logoutState

    fun logout(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                _logoutState.value = !it.isSuccessful
            }
    }

    init {
        getReminders()
    }

    fun getReminders(){
        _showLoadingMutableLiveData.postValue(true)

        viewModelScope.launch{

            val result = dataSourceReminder.getItems()
            when(result){
                is ResultDatabase.Success -> {
                    _reminders.value = result.data.orEmpty()
                    _showLoadingMutableLiveData.postValue(false)
                }
                is ResultDatabase.Error ->{
                    _reminders.value = arrayListOf()
                    _errorMutableLiveData.value = result.message.orEmpty()
                    _showLoadingMutableLiveData.postValue(false)
                }
            }
        }
    }
}
