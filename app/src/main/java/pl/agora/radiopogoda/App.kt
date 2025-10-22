package pl.agora.radiopogoda

import android.app.Application
import com.gemius.sdk.Config
import com.gemius.sdk.GemiusSdk
import com.gemius.sdk.audience.AudienceConfig
import com.gemius.sdk.utils.Duration
import dagger.hilt.android.HiltAndroidApp
import pl.agora.radiopogoda.infrastructure.services.ads.AdsService
import pl.agora.radiopogoda.utils.Consts
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var adsService: AdsService

    override fun onCreate() {
        super.onCreate()
        adsService.adsModuleInitialization()
        initGemius()
    }

    private fun initGemius() {
        GemiusSdk.init(this)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        Config.setAppInfo(getString(R.string.gemius_app_name), packageInfo.versionName)
        Config.setCmpTimeout(Duration.of(30, TimeUnit.SECONDS))
        Config.useCmp(true)
        if (BuildConfig.DEBUG) { Config.setLoggingEnabled(true) }

        AudienceConfig.getSingleton().hitCollectorHost = Consts.GEMIUS_URL
        AudienceConfig.getSingleton().scriptIdentifier = getString(R.string.gemius_audience_id)
    }
}