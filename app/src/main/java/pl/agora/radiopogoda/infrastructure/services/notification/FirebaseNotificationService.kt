package pl.agora.radiopogoda.infrastructure.services.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.R
import javax.inject.Inject

class FirebaseNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManager
): BaseNotificationService(context, notificationManager) {

    override val channelId: String = "FIREBASE_NOTIFICATION_CHANNEL"

    override val channelNameResId: Int = R.string.firebase_channel_name

    override val channelDescriptionResId: Int = R.string.firebase_channel_description

    fun postFirebaseNotification(map: Map<String, String>) {
        val title = map[REMOTE_MESSAGE_TITLE_KEY]
        val message = map[REMOTE_MESSAGE_BODY_KEY]
        val image = map[REMOTE_MESSAGE_IMAGE_KEY]
        val url = map[REMOTE_MESSAGE_URL_KEY]

        val pendingNotificationIntent = PendingIntent.getActivity(
            context, 0,
            Intent(Intent.ACTION_VIEW, url?.toUri()),
            PendingIntent.FLAG_MUTABLE.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }
                ?: PendingIntent.FLAG_ONE_SHOT
        )

        CoroutineScope(Dispatchers.IO).launch {
            val contentView = getFirebaseRemoteViews(title, message, image)

            val notification = NotificationCompat.Builder(context, channelId)
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setContentIntent(pendingNotificationIntent)
                .build()

            postNotification(FIREBASE_NOTIFICATION_ID, notification)
        }
    }

    private suspend fun getFirebaseRemoteViews(title: String?, message: String?, image: String?): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.fcm_notification)
        image?.let { remoteViews.setImageViewBitmap(R.id.fcm_app_logo, loadImageIfNeeded(it)) }
        remoteViews.setTextViewText(R.id.fcm_title, title ?: context.getString(R.string.app_name))
        remoteViews.setTextViewText(R.id.fcm_desc, message ?: "")
        return remoteViews
    }

    companion object {
        private const val FIREBASE_NOTIFICATION_ID = 12635832
        private const val REMOTE_MESSAGE_TITLE_KEY = "title"
        private const val REMOTE_MESSAGE_BODY_KEY = "body"
        private const val REMOTE_MESSAGE_IMAGE_KEY = "image-url"
        private const val REMOTE_MESSAGE_URL_KEY = "url"
    }
}