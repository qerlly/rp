package pl.agora.radiopogoda.ui.advert

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.widget.VideoView
import androidx.core.net.toUri
import com.google.ads.interactivemedia.v3.api.AdPodInfo
import com.google.ads.interactivemedia.v3.api.player.AdMediaInfo
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer.VideoAdPlayerCallback
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate
import java.util.Timer
import java.util.TimerTask

class VideoAdPlayerAdapter(
    private val videoPlayer: VideoView,
    private val audioManager: AudioManager
) : VideoAdPlayer {

    private val videoAdPlayerCallbacks: MutableList<VideoAdPlayerCallback> = ArrayList()
    private var loadedAdMediaInfo: AdMediaInfo? = null
    private var timer: Timer? = null

    private var adDuration = 0
    private var savedAdPosition = 0
    var status: VastStatus = VastStatus.NOT_INIT

    init { videoPlayer.setOnCompletionListener { notifyImaOnContentCompleted() } }

    fun pause() { loadedAdMediaInfo?.let { pauseAd(it) } }

    fun resume() { loadedAdMediaInfo?.let { playAd(it) } }

    override fun addCallback(videoAdPlayerCallback: VideoAdPlayerCallback) {
        videoAdPlayerCallbacks.add(videoAdPlayerCallback)
    }

    override fun loadAd(adMediaInfo: AdMediaInfo, adPodInfo: AdPodInfo) {
        loadedAdMediaInfo = adMediaInfo
    }

    override fun pauseAd(adMediaInfo: AdMediaInfo) {
        status = VastStatus.PAUSED
        savedAdPosition = videoPlayer.currentPosition
        stopAdTracking()
    }

    override fun playAd(adMediaInfo: AdMediaInfo) {
        videoPlayer.setVideoURI(adMediaInfo.url.toUri())
        videoPlayer.setOnPreparedListener { mediaPlayer: MediaPlayer ->
            adDuration = mediaPlayer.duration
            if (savedAdPosition > 0) mediaPlayer.seekTo(savedAdPosition)
            status = VastStatus.IN_PROGRESS
            mediaPlayer.start()
            startAdTracking()
        }
        videoPlayer.setOnErrorListener { _: MediaPlayer?, errorType: Int, _: Int ->
            notifyImaSdkAboutAdError(errorType)
        }
        videoPlayer.setOnCompletionListener {
            savedAdPosition = 0
            notifyImaSdkAboutAdEnded()
        }
    }

    override fun release() {}

    override fun removeCallback(videoAdPlayerCallback: VideoAdPlayerCallback) {
        videoAdPlayerCallbacks.remove(videoAdPlayerCallback)
    }

    override fun stopAd(adMediaInfo: AdMediaInfo) { stopAdTracking() }

    override fun getVolume(): Int {
        return (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
    }

    private fun startAdTracking() {
        if (timer != null) {
            return
        }
        timer = Timer()
        val updateTimerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val progressUpdate = adProgress
                notifyImaSdkAboutAdProgress(progressUpdate)
            }
        }
        timer?.schedule(updateTimerTask, POLLING_TIME_MS, INITIAL_DELAY_MS)
    }

    private fun notifyImaSdkAboutAdEnded() {
        savedAdPosition = 0
        for (callback in videoAdPlayerCallbacks) {
            loadedAdMediaInfo?.let { callback.onEnded(it)  }
        }
    }

    private fun notifyImaSdkAboutAdProgress(adProgress: VideoProgressUpdate) {
        for (callback in videoAdPlayerCallbacks) {
            loadedAdMediaInfo?.let {  callback.onAdProgress(it, adProgress) }
        }
    }

    private fun notifyImaSdkAboutAdError(errorType: Int): Boolean {
        Log.e(LOGTAG, "notifyImaSdkAboutAdError: $errorType")
        for (callback in videoAdPlayerCallbacks) {
            loadedAdMediaInfo?.let { callback.onError(it) } }
        return true
    }

    private fun notifyImaOnContentCompleted() {
        Log.i(LOGTAG, "notifyImaOnContentCompleted")
        for (callback in videoAdPlayerCallbacks) { callback.onContentComplete() }
    }

    private fun stopAdTracking() {
        Log.i(LOGTAG, "stopAdTracking")
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    override fun getAdProgress(): VideoProgressUpdate {
        val adPosition = videoPlayer.currentPosition.toLong()
        return VideoProgressUpdate(adPosition, adDuration.toLong())
    }

    enum class VastStatus { NOT_INIT, PAUSED, IN_PROGRESS }

    companion object {
        private const val LOGTAG = "VAST"
        private const val POLLING_TIME_MS: Long = 250
        private const val INITIAL_DELAY_MS: Long = 250
    }
}