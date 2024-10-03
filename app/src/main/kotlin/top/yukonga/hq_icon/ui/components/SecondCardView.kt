package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import top.yukonga.hq_icon.R
import top.yukonga.hq_icon.data.Data
import top.yukonga.hq_icon.utils.Preferences

@Composable
fun SecondCardView(
    platformCode: MutableState<String>,
    cornerCode: MutableState<String>,
    resolutionCode: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            PlatformView(platformCode)
        }
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            CornerView(cornerCode)
        }
    }
    Card(
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        ResolutionView(resolutionCode)
    }
}

@Composable
fun PlatformView(
    platformCode: MutableState<String>
) {
    val platform = Data().platformNames
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.platform)
        )
        val platformName = Preferences().perfGet("platform")?.let { Data().platformName(it) } ?: platform[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(platformName) }
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            platform.forEach { text ->
                Row(
                    Modifier.selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            hapticFeedback.performHapticFeedback(LongPress)
                            platformCode.value = Data().platformCode(text)
                        },
                        role = Role.RadioButton
                    )
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CornerView(
    cornerCode: MutableState<String>
) {
    val corner = Data().cornerNames
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.corner)
        )
        val cornerName = Preferences().perfGet("corner")?.let { Data().cornerName(it) } ?: corner[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(cornerName) }
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            corner.forEach { text ->
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                                hapticFeedback.performHapticFeedback(LongPress)
                                cornerCode.value = Data().cornerCode(text)
                                Preferences().perfSet("corner", cornerCode.value)
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ResolutionView(
    resolutionCode: MutableState<String>
) {
    val resolution = Data().resolutionNames
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.resolution)
        )
        val resolutionName = Preferences().perfGet("resolution")?.let { Data().resolutionName(it) } ?: resolution[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(resolutionName) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            resolution.forEach { text ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                                hapticFeedback.performHapticFeedback(LongPress)
                                resolutionCode.value = Data().resolutionCode(text)
                                Preferences().perfSet("resolution", resolutionCode.value)
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}