package pl.agora.radiopogoda.infrastructure.services.ads

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.agora.radiopogoda.infrastructure.ads.BaseAdsManager
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.activities.MainActivity
import pl.agora.radiopogoda.ui.activities.VASTActivity
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.Consts.VAST_LINK
import pl.agora.radiopogoda.utils.XMLHelper
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsService @Inject constructor(
    @ApplicationContext private val context: Context,
): BaseAdsManager(context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val canShowAd = MutableStateFlow(true)
    private val canShowVAST = MutableStateFlow(true)
    private val activityAfterVast = MutableStateFlow<(() -> Unit)?>(null)
    private var vastStringState: String? = null
    private val shouldShowVast = MutableStateFlow(false)

    suspend fun showVAST(state: MusicServiceState, onAdDismissed: () -> Unit) {
        if (canShowVAST.value && state != MusicServiceState.PLAY) {
            val vastString = parseVast()
            if (vastString == null) {
                onAdDismissed()
                onVastEnded()
            } else {
                vastStringState = vastString
                shouldShowVast.value = true
                activityAfterVast.value = onAdDismissed
            }
        } else {
            onAdDismissed()
        }
    }

    fun onVastEnded() {
        activityAfterVast.value?.invoke()
        activityAfterVast.value = null
        vastStringState = null
        shouldShowVast.value = false
        canShowVAST.value = false

        coroutineScope.launch {
            delay(delayAd)
            canShowVAST.value = true
        }
    }

    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (!canShowAd.value) {
            onAdDismissed()
            return
        }

        canShowAd.value = false
        showInterstitial(activity) {
            onAdDismissed()
            coroutineScope.launch {
                delay(delayAd)
                canShowAd.value = true
            }
        }
    }

    fun initConsentInformation(activity: Activity, reset: Boolean = false) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        if (reset) consentInformation.reset()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    if (it != null) {
                        Log.w(
                            MainActivity::class.java.simpleName, String.format(
                                "%s: %s",
                                it.errorCode,
                                it.message
                            )
                        )
                    }
                }
            },
            { requestConsentError: FormError ->
                Log.d(
                    MainActivity::class.java.simpleName, String.format(
                        "%s: %s",
                        requestConsentError.errorCode,
                        requestConsentError.message
                    )
                )
            }
        )
    }

    suspend fun observeForShowVast(activity: Activity, resultLauncher: ActivityResultLauncher<Intent>) {
        shouldShowVast.filter { it }
            .collect {
                shouldShowVast.value = false
                val intent = Intent(activity, VASTActivity::class.java)
                intent.putExtra(Consts.VAST_XML_TAG, vastStringState)
                resultLauncher.launch(intent)
            }
    }

    private suspend fun parseVast(): String? = try {
        withContext(Dispatchers.IO) {
            XMLHelper.fetchXmlFromUrl(VAST_LINK, "application/xml")?.let { xml ->
                val rootElement = XMLHelper.parseXml(xml)
                if (XMLHelper.containsTag(rootElement, "AdSystem")) xml else null
            }
        }
    } catch (e: Exception) { null }

    companion object {
        private val delayAd = Duration.ofMinutes(10).toMillis()
    }
}