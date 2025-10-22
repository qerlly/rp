package pl.agora.radiopogoda.ui.composables.views.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.R

@Composable
fun ContactColumn() = Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
){
    Icon(
        painter = painterResource(R.drawable.logo),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(130.dp)
    )
    Text(
        text = ContactData.street,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    Text(
        text = ContactData.city,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    Text(
        text = ContactData.phoneFirst,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    Text(
        text = ContactData.phoneSecond,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
}

object ContactData {
    const val street = "ul. Czerska 8/10"
    const val city = "00-732 Warszawa"
    const val phoneFirst = "tel. (22) 5555 100"
    const val phoneSecond = "e-mail: kontakt@radiopogoda.pl"
}