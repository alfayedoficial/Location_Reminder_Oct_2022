package com.alialfayed.locationreminder.ui.home.viewModel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser

class OneSingleViewModel : ViewModel() {

    private val _logoutState = MutableLiveData<Boolean?>()
    val logoutState: LiveData<Boolean?> = _logoutState

    fun logout(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                _logoutState.value = !it.isSuccessful
            }
    }
}
