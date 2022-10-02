package com.alialfayed.locationreminder.core.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.alialfayed.locationreminder.data.dto.ReminderDTO
import com.alialfayed.locationreminder.data.dto.ResultDatabase
import com.alialfayed.locationreminder.domain.entity.ReminderEntity
import com.alialfayed.locationreminder.domain.usecase.ReminderDataSource
import com.alialfayed.locationreminder.utils.AppConstant.Geofence_JOB_ID
import com.alialfayed.locationreminder.utils.sendNotificationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceTransitionsJobIntentService::class.java, Geofence_JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val geoFence = GeofencingEvent.fromIntent(intent)
        if(geoFence != null && geoFence.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            sendNotification(geoFence.triggeringGeofences!!)
        }
    }

    private fun sendNotification(geofence: List<Geofence>) {
        for (id in geofence) {
            val resultId = id.requestId
            val remindersLocalRepository: ReminderDataSource by inject()
            CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                //get the reminder with the request id
                val result = remindersLocalRepository.getItemById(resultId)
                if (result is ResultDatabase.Success<*>) {
                    val reminderDTO = result.data as ReminderDTO
                    //send a notification to the user with the reminder details
                    sendNotificationUtils(this@GeofenceTransitionsJobIntentService , ReminderEntity(
                        title = reminderDTO.title,
                        description = reminderDTO.description,
                        address = reminderDTO.address,
                        location = LatLng(reminderDTO.latitude!!, reminderDTO.longitude!!))
                    )
                }
            }
        }

    }

}
