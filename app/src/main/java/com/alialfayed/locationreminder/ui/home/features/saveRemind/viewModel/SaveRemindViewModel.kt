package com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.domain.entity.ReminderEntity
import com.alialfayed.locationreminder.domain.dataSource.ReminderDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class SaveRemindViewModel(private val dataSourceReminder: ReminderDataSource): ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState

    private val _errorState = MutableLiveData<Int>()
    val errorState = _errorState

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val location = MutableLiveData<LatLng?>()


    val reminderEntity = MutableLiveData<ReminderEntity?>()


    fun saveReminderEntity() {
        val model = ReminderEntity().apply {
            this.title = this@SaveRemindViewModel.title.value
            this.description = this@SaveRemindViewModel.description.value
            this.address = this@SaveRemindViewModel.address.value
            this.location =this@SaveRemindViewModel.location.value
        }

        reminderEntity.postValue(model)
    }

    fun saveReminderToDatabase() {
        _loadingState.postValue(true)
        viewModelScope.launch {
            reminderEntity.value?.let {
                dataSourceReminder.insertITem(ReminderDTO(
                    title = it.title,
                    description = it.description,
                    address = it.address,
                    latitude = it.location!!.latitude,
                    longitude = it.location!!.longitude))

            }
            _loadingState.postValue(false)
        }
    }

    fun cleanData() {
        title.value = ""
        description.value = ""
        address.value = ""
        location.value = null
        reminderEntity.value = null
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredData(): Boolean {
        if (title.value.isNullOrEmpty()) {
            _errorState.value = R.string.err_enter_title
            return false
        }

        if (description.value.isNullOrEmpty()) {
            _errorState.value = R.string.err_select_location
            return false
        }
        return true
    }
}
