package pl.agora.radiopogoda.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.gemius.sdk.audience.BaseEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.infrastructure.AppPreferences
import pl.agora.radiopogoda.infrastructure.analytics.AnalyticsKey
import pl.agora.radiopogoda.infrastructure.analytics.GemiusEventAudienceManager
import pl.agora.radiopogoda.infrastructure.services.ads.AdsService
import pl.agora.radiopogoda.ui.composables.navigation.AppFeaturesNavigation
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.theme.LightColorPalette
import pl.agora.radiopogoda.ui.viewModels.MainViewModel
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.openUrl
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var appPreferences: AppPreferences
    @Inject lateinit var adsService: AdsService
    @Inject lateinit var gemiusEventAudienceManager: GemiusEventAudienceManager
    private val mainViewModel: MainViewModel by viewModels()

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> if (result.resultCode == RESULT_OK) { adsService.onVastEnded() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { false }
        )
        super.onCreate(savedInstanceState)
        installSplashScreen().also { onNewAudienceEvent(Destinations.splash) }
        setContent { AppUI() }

        askForNotificationPermission()
        checkForFcmMessage()
        lifecycleScope.launch { adsService.observeForShowVast(this@MainActivity, resultLauncher) }
        adsService.initConsentInformation(this@MainActivity)
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lifecycleScope.launch {
                val isFirstLaunch = appPreferences.isFirstLaunch.first()
                if (!isFirstLaunch) {
                    appPreferences.setFirstLaunch(true)
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            12211221
                        )
                    }
                }
            }
        }
    }

    private fun checkForFcmMessage() {
        intent.extras?.getString(Consts.LINK_FROM_FCM)?.let {
            Intent(Intent.ACTION_VIEW, it.toUri()).openUrl(this)
        }
    }

    private fun onNewAudienceEvent(destination: String) = gemiusEventAudienceManager.onNewAudienceEvent(
        key = AnalyticsKey.SCREEN,
        value = destination,
        eventType = BaseEvent.EventType.FULL_PAGEVIEW
    )

    private fun showAd(onAdDismissed: ()-> Unit) = lifecycleScope.launch {
        adsService.showAd(this@MainActivity, onAdDismissed)
    }

    private fun resetConsentForm() {
        val reset = true
        adsService.initConsentInformation(this@MainActivity, reset)
        gemiusEventAudienceManager.onNewAudienceEvent(
            key = AnalyticsKey.RESET_CONSENT,
            value = reset.toString(),
            eventType = BaseEvent.EventType.ACTION
        )
    }

    @Composable
    fun AppUI() {
        val onNewAudienceEvent = remember { { destination: String -> onNewAudienceEvent(destination) } }
        val resetConsent = remember { { resetConsentForm() } }
        val showAd = remember { { onAdDismissed: () -> Unit -> showAd(onAdDismissed) } }

        MaterialTheme(LightColorPalette) {
            AppFeaturesNavigation(onNewAudienceEvent, resetConsent, showAd, mainViewModel)
        }
    }
}