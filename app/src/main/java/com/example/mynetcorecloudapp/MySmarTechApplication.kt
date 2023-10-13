package com.example.mynetcorecloudapp

import android.app.Application
import android.util.Log
import com.netcore.android.Smartech
import com.netcore.android.smartechpush.SmartPush
import java.lang.ref.WeakReference

class MySmarTechApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Smartech.getInstance(WeakReference(applicationContext)).initializeSdk(this)
        Smartech.getInstance(WeakReference(applicationContext)).setDebugLevel(9)

        try {
            val smartPush = SmartPush.getInstance(WeakReference(applicationContext))
            smartPush.fetchAlreadyGeneratedTokenFromFCM()
        } catch (e: Exception) {
            Log.e("FCM", "Fetching FCM token failed.")
        }
    }
}