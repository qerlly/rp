package pl.agora.radiopogoda.ui.activities

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.google.ads.interactivemedia.v3.api.AdsLoader
import com.google.ads.interactivemedia.v3.api.AdsManager
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.advert.VideoAdPlayerAdapter
import pl.agora.radiopogoda.utils.Consts

class VASTActivity : AppCompatActivity() {

    private lateinit var videoPlayer: VideoView
    private lateinit var progressBar: View
    private lateinit var playIcon: View

    private lateinit var audioManager: AudioManager
    private lateinit var mediaController: MediaController
    private lateinit var sdkFactory: ImaSdkFactory
    private lateinit var videoAdPlayerAdapter: VideoAdPlayerAdapter
    private lateinit var adsLoader: AdsLoader
    private var vastXml: String? = null
    private var adsManager: AdsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blockBackPressedButton()

        val vast = intent.getStringExtra(Consts.VAST_XML_TAG)

        if (vast == null) closeVast()
        else vastXml = vast

        setContentView(R.layout.vast_activity)
        videoPlayer = findViewById(R.id.videoView)
        progressBar = findViewById(R.id.progressBar)
        playIcon = findViewById(R.id.playIcon)

        try {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            initVastComponents()
            requestAds()
        } catch (e: Exception) {
            closeVast()
        }
    }

    override fun onResume() {
        super.onResume()
        playIcon.visibility = View.GONE
    }

    private fun blockBackPressedButton() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initVastComponents() {
        mediaController = MediaController(this)
        sdkFactory = ImaSdkFactory.getInstance()

        mediaController.setAnchorView(videoPlayer)
        videoPlayer.setMediaController(mediaController)

        val videoPlayerContainer = findViewById<ViewGroup>(R.id.videoPlayerContainer)
        videoAdPlayerAdapter = VideoAdPlayerAdapter(videoPlayer, audioManager)

        val settings = sdkFactory.createImaSdkSettings()
        val adDisplayContainer = ImaSdkFactory.createAdDisplayContainer(videoPlayerContainer, videoAdPlayerAdapter)
        adsLoader = sdkFactory.createAdsLoader(this, settings, adDisplayContainer)

        adsLoader.addAdErrorListener { adErrorEvent ->
            Log.i(LOGTAG, "Ad Error: " + adErrorEvent.error.message)
            closeVast()
        }

        adsLoader.addAdsLoadedListener { adsManagerLoadedEvent ->
            adsManager = adsManagerLoadedEvent.adsManager.apply {
                addAdErrorListener {
                    discardAdBreak()
                    closeVast()
                }
                addAdEventListener { adEvent ->
                    when (adEvent.type) {
                        AdEvent.AdEventType.LOADED ->  {
                            progressBar.visibility = View.GONE
                            start()
                        }
                        AdEvent.AdEventType.ALL_ADS_COMPLETED -> {
                            destroy()
                            closeVast()
                        }
                        AdEvent.AdEventType.TAPPED -> {
                            if (videoAdPlayerAdapter.status == VideoAdPlayerAdapter.VastStatus.IN_PROGRESS)
                                pauseVast()
                            else if (videoAdPlayerAdapter.status == VideoAdPlayerAdapter.VastStatus.PAUSED)
                                resumeVast()
                        }
                        else -> {}
                    }
                }
            }
            val adsRenderingSettings = ImaSdkFactory.getInstance().createAdsRenderingSettings()
            adsManager?.init(adsRenderingSettings)
        }
    }

    private fun resumeVast() {
        playIcon.visibility = View.GONE
        videoAdPlayerAdapter.resume()
        videoPlayer.resume()
    }

    private fun pauseVast() {
        playIcon.visibility = View.VISIBLE
        videoAdPlayerAdapter.pause()
        videoPlayer.pause()
    }

    private fun closeVast() {
        adsManager = null
        setResult(RESULT_OK)
        finish()
    }

    private fun requestAds() {
        val request = sdkFactory.createAdsRequest()
        vastXml?.let { request.adsResponse = it }

        request.contentProgressProvider = ContentProgressProvider {
            if (videoPlayer.duration <= 0) { VideoProgressUpdate.VIDEO_TIME_NOT_READY }
            else VideoProgressUpdate(videoPlayer.currentPosition.toLong(), videoPlayer.duration.toLong())
        }

        adsLoader.requestAds(request)
    }

    companion object {
        private const val LOGTAG = "VAST"
    }
}