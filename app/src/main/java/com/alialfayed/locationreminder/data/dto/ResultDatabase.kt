package com.alialfayed.locationreminder.data.dto


/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class ResultDatabase<out T : Any?> {
    data class Success<out T : Any>(val data: T?) : ResultDatabase<T>()
    data class Error(val message: String?, val statusCode: Int? = null) : ResultDatabase<Nothing>()
}