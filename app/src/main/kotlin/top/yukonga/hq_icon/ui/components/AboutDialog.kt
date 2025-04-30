package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import top.yukonga.hq_icon.BuildConfig
import top.yukonga.hq_icon.R
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Info
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog

@Composable
fun AboutDialog() {
    val showDialog = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    IconButton(
        modifier = Modifier.padding(end = 12.dp),
        onClick = {
            showDialog.value = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
        },
        holdDownState = showDialog.value
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            imageVector = MiuixIcons.Useful.Info,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
        )
    }

    SuperDialog(
        title = stringResource(R.string.app_name),
        summary = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
        content = {
            Column {
                val uriHandler = LocalUriHandler.current
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.view_source) + " "
                    )
                    Text(
                        text = AnnotatedString(
                            text = "GitHub",
                            spanStyle = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MiuixTheme.colorScheme.primary
                            )
                        ),
                        modifier = Modifier.clickable(
                            onClick = {
                                uriHandler.openUri("https://github.com/YuKongA/HQ-ICON_Compose")
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.join_group) + " "
                    )
                    Text(
                        text = AnnotatedString(
                            text = "Telegram",
                            spanStyle = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MiuixTheme.colorScheme.primary
                            )
                        ),
                        modifier = Modifier.clickable(
                            onClick = {
                                uriHandler.openUri("https://t.me/YuKongA13579")
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                            },
                        )
                    )
                }
            }
        }
    )
}