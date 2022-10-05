package com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.domain.entity.ReminderEntity
import com.alialfayed.locationreminder.domain.dataSource.ReminderDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class SaveRemindViewModel(private val dataSourceReminder: ReminderDataSource): ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val location = MutableLiveData<LatLng?>()


    val reminderEntity = MutableLiveData<ReminderEntity?>()

    val saveRemindState = MediatorLiveData<Boolean>().apply {
        addSource(title) {
            value = !(title.value == null || title.value!!.isEmpty() ||title.value!!.length < 3 || description.value == null || description.value!!.length < 3 || description.value!!.isEmpty() || address.value == null || address.value!!.isEmpty() || location.value == null)
         }
        addSource(description) {
            value = !(title.value == null || title.value!!.isEmpty() || title.value!!.length < 3 || description.value == null || description.value!!.length < 3 ||  description.value!!.isEmpty() || address.value == null || address.value!!.isEmpty() || location.value == null)
        }
        addSource(address) {
            value = !(title.value == null || title.value!!.isEmpty() || title.value!!.length < 3 || description.value == null || description.value!!.length < 3 ||  description.value!!.isEmpty() || address.value == null || address.value!!.isEmpty() || location.value == null)
        }
        addSource(location) {
            value = !(title.value == null || title.value!!.isEmpty() || title.value!!.length < 3 || description.value == null || description.value!!.length < 3 || description.value!!.isEmpty() || address.value == null || address.value!!.isEmpty() || location.value == null)
        }
    }


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
        viewModelScope.launch {
            reminderEntity.value?.let {
                _loadingState.postValue(true)
                dataSourceReminder.insertITem(ReminderDTO(
                    title = it.title,
                    description = it.description,
                    address = it.address,
                    latitude = it.location!!.latitude,
                    longitude = it.location!!.longitude))

                _loadingState.postValue(false)
            }
        }
    }

    fun cleanData() {
        title.value = ""
        description.value = ""
        address.value = ""
        location.value = null
        reminderEntity.value = null
    }
}
