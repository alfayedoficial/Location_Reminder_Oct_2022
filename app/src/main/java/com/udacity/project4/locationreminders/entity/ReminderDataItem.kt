package com.udacity.project4.locationreminders.entity

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.*


data class ReminderDataItem(
    var title: String? = null,
    var description: String?= null,
    var location: String?= null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var id: String = UUID.randomUUID().toString()
): Serializable