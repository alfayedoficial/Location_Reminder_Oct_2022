package com.udacity.project4.ui.splash.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.udacity.project4.R
import com.udacity.project4.core.common.app.BaseApp.Companion.appPreferences
import com.udacity.project4.ui.auth.view.AuthActivity
import com.udacity.project4.utils.AppConstant.USER
import com.udacity.project4.ui.home.view.OneSingleActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }
        if (appPreferences.getStringValue(USER).isNotEmpty()) {
            startActivity(Intent(this, OneSingleActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }

    }
}