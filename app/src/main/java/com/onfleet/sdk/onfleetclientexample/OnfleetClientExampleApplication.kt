package com.onfleet.sdk.onfleetclientexample

import androidx.multidex.MultiDex
import android.os.Build
import android.os.Build.VERSION_CODES
import android.annotation.TargetApi
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.onfleet.sdk.managers.CoreManager
import timber.log.Timber
import timber.log.Timber.Forest.plant

class OnfleetClientExampleApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        initOnfleetSDK()
    }

    private fun initOnfleetSDK() {
        CoreManager.getInstance().enableTimberLog(Log.VERBOSE)

        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            createChannels()
        }
    }

    @TargetApi(VERSION_CODES.O)
    private fun createChannels() {
        val defaultChannel = NotificationChannel(
            "CHANNEL_DEFAULT",
            "DEFAULT",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        defaultChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(defaultChannel)
    }
}