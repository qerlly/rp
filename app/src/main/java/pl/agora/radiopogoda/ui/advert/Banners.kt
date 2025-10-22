package pl.agora.radiopogoda.ui.advert

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.black

@Composable
fun AdvertViewBig(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    val adView = remember {
        AdView(context).apply {
            adUnitId = context.getString(R.string.banner_ad_id)
            setAdSize(AdSize.MEDIUM_RECTANGLE)
        }
    }

    LaunchedEffect(adView) {
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                isLoading.value = false
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                isLoading.value = false
            }
        }
        adView.loadAd(getGoogleAdRequest())
    }

    Column(modifier) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = black
                )
            }
            AndroidView(
                factory = { adView },
                update = { },
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(Modifier.height(4.dp))
    }
}

private fun getGoogleAdRequest(): AdRequest {
    val extras = Bundle().apply { putString("npa", "1") }
    return AdRequest.Builder()
        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        .build()
}