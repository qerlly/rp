package pl.agora.radiopogoda.ui.composables.views.contact

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.ui.theme.darkWhite
import pl.agora.radiopogoda.ui.theme.indicatorTransparent
import pl.agora.radiopogoda.ui.theme.lightGray
import pl.agora.radiopogoda.ui.theme.secondText
import pl.agora.radiopogoda.utils.openDestination

@Composable
fun ContactView(showConsent: () -> Unit, navController: NavHostController) = Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        NotificationPermissionButton()
    }
    PickCityButton(navController)
    ButtonRODORow(showConsent)
    HorizontalDivider(color = darkWhite)
    ContactColumn()
    HorizontalDivider(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
    )
    FrequenciesColumn()
    HorizontalDivider(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
    )
    SocialRow()
    Spacer(Modifier.height(64.dp))
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionButton() {
    val context = LocalContext.current
    val permissionState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionState.value = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted
    }

    if (!permissionState.value) {
        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            modifier = Modifier.padding(6.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.White)
        ) {
            Text(
                text = stringResource(R.string.push_agree),
                color = black,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun ButtonRODORow(showConsent: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .clickable { showConsent() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.rodo).uppercase(),
            color = black,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = black,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PickCityButton(navController: NavHostController) = Row(Modifier.fillMaxWidth().height(52.dp)) {
    Button(
        onClick = { navController.openDestination(Destinations.cities) },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = indicatorTransparent)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.pick_city).uppercase(),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}