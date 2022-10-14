package com.udacity.project4.locationreminders.features.saveRemind.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.entity.ReminderDataItem
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class SaveReminderViewModel(private val dataSourceReminder: ReminderDataSource): ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState

    private val _errorState = MutableLiveData<Int>()
    val errorState = _errorState

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val location = MutableLiveData<LatLng?>()


    val reminderDataItem = MutableLiveData<ReminderDataItem?>()


    fun saveReminderEntity() {
        val model = ReminderDataItem().apply {
            this.title = this@SaveReminderViewModel.title.value
            this.description = this@SaveReminderViewModel.description.value
            this.location = this@SaveReminderViewModel.address.value
            this.latitude =this@SaveReminderViewModel.location.value?.latitude
            this.longitude =this@SaveReminderViewModel.location.value?.longitude
        }

        reminderDataItem.postValue(model)
    }

    fun saveReminderToDatabase() {
        _loadingState.postValue(true)
        viewModelScope.launch {
            reminderDataItem.value?.let {
                dataSourceReminder.saveReminder(
                    ReminderDTO(
                        title = it.title,
                        description = it.description,
                        location = it.location,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                )

            }
            _loadingState.postValue(false)
        }
    }

    fun cleanData() {
        title.value = ""
        description.value = ""
        address.value = ""
        location.value = null
        reminderDataItem.value = null
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredData(): Boolean {
        if (title.value.isNullOrEmpty()) {
            _errorState.value = R.string.err_enter_title
            return false
        }

        if (address.value.isNullOrEmpty()) {
            _errorState.value = R.string.err_select_location
            return false
        }
        return true
    }
}
