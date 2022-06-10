package com.onfleet.sdk.onfleetclientexample

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.onfleet.sdk.dataModels.OnfleetFcmNotification
import com.onfleet.sdk.managers.OnfleetFcmManager
import timber.log.Timber

class FcmPushReceiverService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val onfleetNotification: OnfleetFcmNotification = OnfleetFcmManager.getInstance()
            .onOnfleetFcmNotificationReceived(remoteMessage.data, remoteMessage.from)
        Timber.d(onfleetNotification.toString())
        //handle and show OnfleetNotification
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        OnfleetFcmManager.getInstance().handleTokenRefresh(token)
    }
}