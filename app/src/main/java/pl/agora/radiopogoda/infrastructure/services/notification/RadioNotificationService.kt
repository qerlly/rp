package pl.agora.radiopogoda.infrastructure.services.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import pl.agora.radiopogoda.infrastructure.services.music.MusicService
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceActions
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.activities.MainActivity
import pl.agora.radiopogoda.utils.getPendingIntent
import javax.inject.Inject

class RadioNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManager
): BaseNotificationService(context, notificationManager) {

    override val channelId: String = "RADIO_NOTIFICATION_CHANNEL"

    override val channelNameResId: Int = R.string.radio_channel_name

    override val channelDescriptionResId: Int = R.string.radio_channel_description

    private fun buildRemoteViews(state: MusicServiceState, data: PlayerMediaItemModel?): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.radio_notification)
        val name = when (data?.mediaType) {
            MediaType.MAIN_CHANNEL -> context.getString(R.string.app_name)
            MediaType.PODCAST -> data.title
            else -> data?.subtitle ?: context.getString(R.string.app_name)
        }

        data?.let { infoModel ->
            remoteViews.setTextViewText(R.id.notification_channel_name, name)
            remoteViews.setTextViewText(R.id.track_title, infoModel.title)
            remoteViews.setTextViewText(R.id.track_author, infoModel.author)
            if (infoModel.imageUri == cachedImageUrl && cachedImage != null) {
                remoteViews.setImageViewBitmap(R.id.notification_image, cachedImage)
            }
        }
        manageServiceState(state, remoteViews)
        remoteViews.setOnClickPendingIntent(R.id.notification_click, createPendingIntent())
        return remoteViews
    }

    private fun buildNotification(remoteViews: RemoteViews): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setSmallIcon(R.drawable.logo)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setOngoing(true)
            .setPriority(PRIORITY_MAX)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(createPendingIntent())
            .build()
    }

    fun postNotification(state: MusicServiceState, data: PlayerMediaItemModel?) {
        postNotification(MusicService.FOREGROUND_SERVICE_ID, prepareNotification(state, data))

        data?.imageUri?.let { url ->
            if (cachedImageUrl == url && cachedImage != null) return@let

            CoroutineScope(Dispatchers.IO).launch {
                val loadedBitmap = loadImageIfNeeded(url)
                if (loadedBitmap != null) {
                    withContext(Dispatchers.Main) {
                        val updatedRemoteViews = buildRemoteViews(state, data)
                        updatedRemoteViews.setImageViewBitmap(R.id.notification_image, loadedBitmap)
                        postNotification(MusicService.FOREGROUND_SERVICE_ID, buildNotification(updatedRemoteViews))
                    }
                }
            }
        }
    }

    fun prepareNotification(state: MusicServiceState, data: PlayerMediaItemModel?): Notification {
        createNotificationChannel()
        val remoteViews = buildRemoteViews(state, data)
        return buildNotification(remoteViews)
    }

    private fun createPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun manageServiceState(state: MusicServiceState, remoteViews: RemoteViews) {
        val pauseIntent = Intent(context, MusicService::class.java).apply {
            action = MusicServiceActions.PAUSE_NOTIFICATION.name
        }.getPendingIntent(context)

        val playIntent = Intent(context, MusicService::class.java).apply {
            action = MusicServiceActions.PLAY_NOTIFICATION.name
        }.getPendingIntent(context)

        val stateConfig = when (state) {
            MusicServiceState.PAUSE, MusicServiceState.PAUSED_FROM_RECEIVERS ->
                Pair(Pair(View.INVISIBLE, View.VISIBLE), Pair(playIntent, R.drawable.ic_play_small))
            MusicServiceState.PLAY ->
                Pair(Pair(View.INVISIBLE, View.VISIBLE), Pair(pauseIntent, R.drawable.ic_pause_small))
            MusicServiceState.PREPARE ->
                Pair(Pair(View.VISIBLE, View.INVISIBLE), Pair(pauseIntent, R.drawable.ic_pause_small))
            else -> return
        }

        remoteViews.setViewVisibility(R.id.notification_progress, stateConfig.first.first)
        remoteViews.setViewVisibility(R.id.notification_button, stateConfig.first.second)
        remoteViews.setOnClickPendingIntent(R.id.notification_button, stateConfig.second.first)
        remoteViews.setImageViewResource(R.id.notification_button, stateConfig.second.second)
    }
}