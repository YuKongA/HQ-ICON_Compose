package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.yukonga.hq_icon.R
import top.yukonga.hq_icon.data.Data

@Composable
fun MainCardView(
    appName: MutableState<String>,
    country: MutableState<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppNameView(appName)
        CountryView(country)
    }
}

@Composable
fun AppNameView(
    appName: MutableState<String>
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = appName.value,
        onValueChange = { appName.value = it },
        label = { Text(stringResource(R.string.appName)) },
        shape = RoundedCornerShape(15.dp),
        maxLines = 1,
        leadingIcon = { Icon(imageVector = Icons.Outlined.ImageSearch, null) },
    )
}

@Composable
fun CountryView(
    country: MutableState<String>
) {
    TextFieldWithDropdown(
        text = country,
        items = Data().country,
        label = stringResource(R.string.country),
        leadingIcon = Icons.Outlined.TravelExplore,
    )
}
