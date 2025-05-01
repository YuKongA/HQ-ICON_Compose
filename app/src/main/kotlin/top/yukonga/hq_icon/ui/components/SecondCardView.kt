package top.yukonga.hq_icon.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.yukonga.hq_icon.R
import top.yukonga.hq_icon.data.Data
import top.yukonga.hq_icon.utils.Preferences
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperCheckbox

@Composable
fun SecondCardView(
    platformCode: MutableState<String>,
    cornerCode: MutableState<String>,
    resolutionCode: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp)
        ) {
            OptionSelectionView(
                titleResId = R.string.platform,
                options = Data().platformNames,
                preferenceKey = "platform",
                nameToCodeConverter = { Data().platformCode(it) },
                codeToNameConverter = { Data().platformName(it) },
                selectedCodeState = platformCode
            )
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp)
        ) {
            OptionSelectionView(
                titleResId = R.string.corner,
                options = Data().cornerNames,
                preferenceKey = "corner",
                nameToCodeConverter = { Data().cornerCode(it) },
                codeToNameConverter = { Data().cornerName(it) },
                selectedCodeState = cornerCode
            )
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        OptionSelectionView(
            titleResId = R.string.resolution,
            options = Data().resolutionNames,
            preferenceKey = "resolution",
            nameToCodeConverter = { Data().resolutionCode(it) },
            codeToNameConverter = { Data().resolutionName(it) },
            selectedCodeState = resolutionCode
        )
    }
}

@Composable
private fun OptionSelectionView(
    @StringRes titleResId: Int,
    options: List<String>,
    preferenceKey: String,
    nameToCodeConverter: (String) -> String,
    codeToNameConverter: (String) -> String?,
    selectedCodeState: MutableState<String>
) {
    val focusManager = LocalFocusManager.current
    val initialName = Preferences().perfGet(preferenceKey)?.let { codeToNameConverter(it) } ?: options.getOrElse(0) { "" }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(initialName) }
    selectedCodeState.value = nameToCodeConverter(selectedOption)
    Column {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            text = stringResource(titleResId)
        )
        Column {
            options.forEach { text ->
                SuperCheckbox(
                    title = text,
                    checked = (text == selectedOption),
                    onCheckedChange = {
                        onOptionSelected(text)
                        val code = nameToCodeConverter(text)
                        selectedCodeState.value = code
                        Preferences().perfSet(preferenceKey, code)
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
}