package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.Confirm
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun TextFieldWithDropdown(
    text: MutableState<String>,
    items: List<String>,
    label: String
) {
    val showPopup = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.onFocusChanged { focusState ->
                showPopup.value = focusState.isFocused
            },
            value = text.value,
            onValueChange = {
                text.value = it
                if (showPopup.value && items.isNotEmpty()) {
                    showPopup.value = true
                }
            },
            singleLine = true,
            label = label,
            backgroundColor = MiuixTheme.colorScheme.surface,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        ListPopup(
            show = showPopup,
            onDismissRequest = {
                focusManager.clearFocus()
                showPopup.value = false
            },
            alignment = PopupPositionProvider.Align.TopLeft,
            windowDimming = false,
            maxHeight = 250.dp
        ) {
            if (items.isNotEmpty()) {
                ListPopupColumn {
                    items.forEachIndexed { index, item ->
                        DropdownImpl(
                            text = item,
                            optionSize = items.size,
                            onSelectedIndexChange = { selectedIndex ->
                                text.value = items[selectedIndex]
                                hapticFeedback.performHapticFeedback(Confirm)
                                focusManager.clearFocus()
                                showPopup.value = false
                            },
                            isSelected = false,
                            index = index,
                        )
                    }
                }
            }
        }
    }
}