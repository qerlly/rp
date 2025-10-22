package pl.agora.radiopogoda.infrastructure.services.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import android.app.NotificationChannel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

abstract class BaseNotificationService(
    private val context: Context,
    private val notificationManager: NotificationManager,
) {

    abstract val channelId: String

    @get:StringRes
    abstract val channelNameResId: Int

    @get:StringRes
    abstract val channelDescriptionResId: Int

    protected var cachedImage: Bitmap? = null

    protected var cachedImageUrl: String? = null

    fun postNotification(id: Int, notification: Notification) {
        createNotificationChannel()
        notificationManager.notify(id, notification)
    }

    fun cancelNotification(id: Int) = notificationManager.cancel(id)

    fun createNotificationChannel() {
        val name = context.getString(channelNameResId)
        val description = context.getString(channelDescriptionResId)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            this.description = description
        }
        notificationManager.createNotificationChannel(channel)
    }

    protected suspend fun loadImageIfNeeded(image: String): Bitmap? {
        if (cachedImageUrl == image && cachedImage != null) return cachedImage
        return withContext(Dispatchers.IO) {
            try {
                val input = URL(image).openStream()
                val decodedBitmap = BitmapFactory.decodeStream(input)
                cachedImage = decodedBitmap
                cachedImageUrl = image
                decodedBitmap
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}