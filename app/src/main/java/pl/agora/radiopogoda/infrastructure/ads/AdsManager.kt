package pl.agora.radiopogoda.infrastructure.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import pl.agora.radiopogoda.R

abstract class BaseAdsManager(private val appContext: Context) {

    private var interstitial: InterstitialAd? = null

    fun adsModuleInitialization() {
        MobileAds.initialize(appContext)
        val configuration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(configuration)

        val consentInformation = ConsentInformation.getInstance(appContext)
        val publisherIds = arrayOf(appContext.resources.getString(R.string.ads_app_id))
        consentInformation.requestConsentInfoUpdate(
            publisherIds,
            object : ConsentInfoUpdateListener {
                override fun onConsentInfoUpdated(consentStatus: ConsentStatus?) {}

                override fun onFailedToUpdateConsentInfo(errorDescription: String) {}
            }
        )
        loadInterstitial()
    }

    private fun loadInterstitial(context: Context = appContext) {
        val extras = Bundle().apply { putString("npa", "1") }
        val adRequestBuilder = AdRequest.Builder()
        adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)

        InterstitialAd.load(
            context,
            context.getString(R.string.interstitial_ad_id),
            adRequestBuilder.build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitial = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitial = interstitialAd
                }
            }
        )
    }

    protected fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
        val activity = generateSequence(context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>()
            .firstOrNull()

        if (interstitial != null && activity != null) {
            interstitial?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    onAdDismissedOrFailed(context, onAdDismissed)
                }

                override fun onAdDismissedFullScreenContent() {
                    onAdDismissedOrFailed(context, onAdDismissed)
                }

                override fun onAdShowedFullScreenContent() {
                    interstitial = null
                }
            }
            interstitial?.show(activity)
        } else {
            onAdDismissedOrFailed(context, onAdDismissed)
        }
    }

    private fun onAdDismissedOrFailed(context: Context, onAdDismissed: () -> Unit) {
        interstitial = null
        loadInterstitial(context)
        onAdDismissed()
        removeInterstitial()
    }

    private fun removeInterstitial() {
        interstitial?.fullScreenContentCallback = null
        interstitial = null
    }
}
