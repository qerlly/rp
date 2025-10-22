package pl.agora.radiopogoda.infrastructure.services.notification

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class CloudMessageService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseNotificationService: FirebaseNotificationService

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty())
            try { firebaseNotificationService.postFirebaseNotification(remoteMessage.data) }
            catch (e: Exception) { e.printStackTrace() }
    }
}