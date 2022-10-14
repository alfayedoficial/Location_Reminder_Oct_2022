package com.udacity.project4.splash.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.udacity.project4.R
import com.udacity.project4.core.common.app.MyApp.Companion.appPreferences
import com.udacity.project4.authentication.view.AuthenticationActivity
import com.udacity.project4.utils.AppConstant.USER
import com.udacity.project4.locationreminders.reminders.RemindersActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }
        if (appPreferences.getStringValue(USER).isNotEmpty()) {
            startActivity(Intent(this, RemindersActivity::class.java))
        } else {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }

    }
}