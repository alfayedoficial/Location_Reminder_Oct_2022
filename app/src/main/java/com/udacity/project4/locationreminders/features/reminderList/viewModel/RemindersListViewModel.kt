package com.udacity.project4.locationreminders.features.reminderList.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.locationreminders.data.dto.Reminders
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.launch

class RemindersListViewModel(private val dataSourceReminder: ReminderDataSource) : ViewModel() {


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

            when(val result = dataSourceReminder.getReminders()){
                is Result.Success -> {
                    _reminders.value = result.data.orEmpty()
                    _showLoadingMutableLiveData.postValue(false)
                }
                is Result.Error ->{
                    _reminders.value = arrayListOf()
                    _errorMutableLiveData.value = result.message.orEmpty()
                    _showLoadingMutableLiveData.postValue(false)
                }
            }
        }
    }
}
