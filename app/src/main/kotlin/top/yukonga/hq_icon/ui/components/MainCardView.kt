package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.hq_icon.R
import top.yukonga.hq_icon.data.Data
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MainCardView(
    appName: MutableState<String>,
    country: MutableState<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppNameView(appName)
        CountryView(country)
    }
}

@Composable
fun AppNameView(
    appName: MutableState<String>
) {
    TextField(
        insideMargin = DpSize(16.dp, 20.dp),
        modifier = Modifier.fillMaxWidth(),
        value = appName.value,
        onValueChange = { appName.value = it },
        label = stringResource(R.string.appName),
        backgroundColor = MiuixTheme.colorScheme.surface,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )
}

@Composable
fun CountryView(
    country: MutableState<String>
) {
    TextFieldWithDropdown(
        text = country,
        items = Data().country,
        label = stringResource(R.string.country)
    )
}
