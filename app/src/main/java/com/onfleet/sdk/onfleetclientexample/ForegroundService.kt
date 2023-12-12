package com.onfleet.sdk.onfleetclientexample

import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import java.lang.UnsupportedOperationException

//Foreground service, that will keep the SDK running properly when it is in background
class ForegroundService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE
                    } else 0
                )
                val notification = NotificationCompat.Builder(this, "CHANNEL_DEFAULT")
                    .setContentTitle("Streaming location")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        111, notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                    )
                } else {
                    startForeground(111, notification)
                }
            }

            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
    }
}