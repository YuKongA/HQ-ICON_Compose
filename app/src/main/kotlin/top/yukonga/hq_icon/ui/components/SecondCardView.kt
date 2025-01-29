package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
            PlatformView(platformCode)
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp)
        ) {
            CornerView(cornerCode)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        ResolutionView(resolutionCode)
    }
}

@Composable
fun PlatformView(
    platformCode: MutableState<String>
) {
    val platform = Data().platformNames

    Column {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp),
            text = stringResource(R.string.platform)
        )
        val platformName = Preferences().perfGet("platform")?.let { Data().platformName(it) } ?: platform[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(platformName) }
        Column {
            platform.forEach { text ->
                SuperCheckbox(
                    insideMargin = PaddingValues(12.dp),
                    title = text,
                    checked = (text == selectedOption),
                    onCheckedChange = {
                        onOptionSelected(text)
                        platformCode.value = Data().platformCode(text)
                    }
                )
            }
        }
    }
}

@Composable
fun CornerView(
    cornerCode: MutableState<String>
) {
    val corner = Data().cornerNames

    Column {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp),
            text = stringResource(R.string.corner)
        )
        val cornerName = Preferences().perfGet("corner")?.let { Data().cornerName(it) } ?: corner[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(cornerName) }
        Column {
            corner.forEach { text ->
                SuperCheckbox(
                    insideMargin = PaddingValues(12.dp),
                    title = text,
                    checked = (text == selectedOption),
                    onCheckedChange = {
                        onOptionSelected(text)
                        cornerCode.value = Data().cornerCode(text)
                    }
                )
            }
        }
    }
}

@Composable
fun ResolutionView(
    resolutionCode: MutableState<String>
) {
    val resolution = Data().resolutionNames

    Column {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp),
            text = stringResource(R.string.resolution)
        )
        val resolutionName = Preferences().perfGet("resolution")?.let { Data().resolutionName(it) } ?: resolution[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(resolutionName) }
        Column {
            resolution.forEach { text ->
                SuperCheckbox(
                    modifier = Modifier,
                    insideMargin = PaddingValues(12.dp),
                    title = text,
                    checked = (text == selectedOption),
                    onCheckedChange = {
                        onOptionSelected(text)
                        resolutionCode.value = Data().resolutionCode(text)
                        Preferences().perfSet("resolution", Data().resolutionCode(text))
                    }
                )
            }
        }
    }
}