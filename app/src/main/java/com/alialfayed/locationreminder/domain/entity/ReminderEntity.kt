package com.alialfayed.locationreminder.domain.entity

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.*


data class ReminderEntity(
    var title: String? = null,
    var description: String?= null,
    var address: String?= null,
    var location: LatLng?= null,
    var id: String = UUID.randomUUID().toString()
): Serializable