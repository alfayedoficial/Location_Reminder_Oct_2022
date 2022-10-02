package com.alialfayed.locationreminder.ui.auth.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.alfayedoficial.kotlinutils.kuClearIntentClass
import com.alfayedoficial.kotlinutils.kuRes
import com.alfayedoficial.kotlinutils.kuSnackBarError
import com.alialfayed.locationreminder.ui.home.view.OneSingleActivity
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.core.common.app.BaseApp.Companion.appPreferences

import com.alialfayed.locationreminder.databinding.ActivityAuthBinding
import com.alialfayed.locationreminder.utils.AppConstant.USER
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val dataBinder get() = _binding!!

    private val mViewModel by viewModels<AuthViewModel>()

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(dataBinder.root)
        dataBinder.activity = this

        setUpViewModelStateObservers()
    }

    private fun setUpViewModelStateObservers() {
        mViewModel.firebaseUserState.observe(this) {
            if (it?.displayName != null) {
                appPreferences.setValue(USER, it)
                kuSnackBarError("Welcome ${it.displayName}" , kuRes.getColor(R.color.white , theme) ,  kuRes.getColor(R.color.TemplateGreen , theme))
                kuClearIntentClass(OneSingleActivity::class.java , Pair(USER , it))
            }
        }
    }


    fun onUiAuthClick() {
        mViewModel.initAuthUI(R.style.GreenTheme , R.drawable.firebase){
            signInLauncher.launch(Intent(it))
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            mViewModel.setFirebaseAuth(user)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            kuSnackBarError(response?.error?.errorCode.toString() , kuRes.getColor(R.color.white , theme) ,  kuRes.getColor(R.color.TemplateRed , theme) )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBinder.unbind()
        _binding = null
    }
}