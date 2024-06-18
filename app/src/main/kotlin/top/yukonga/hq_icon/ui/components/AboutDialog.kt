package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import top.yukonga.hq_icon.BuildConfig
import top.yukonga.hq_icon.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog() {
    var showDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val hapticFeedback = LocalHapticFeedback.current

    IconButton(
        onClick = {
            showDialog = true
            hapticFeedback.performHapticFeedback(LongPress)
        }) {
        Icon(
            imageVector = Icons.Outlined.ImageSearch,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(280.dp, 150.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(24.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column(
                            modifier = Modifier.padding(start = 18.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                modifier = Modifier,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString()),
                                modifier = Modifier,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 88.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(R.string.view_source) + " ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            ClickableText(
                                text = AnnotatedString(
                                    text = "GitHub",
                                    spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
                                ),
                                onClick = {
                                    uriHandler.openUri("https://github.com/YuKongA/HQ-ICON_Compose")
                                    hapticFeedback.performHapticFeedback(LongPress)
                                },
                                style = MaterialTheme.typography.bodyMedium + SpanStyle(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        Row {
                            Text(
                                text = stringResource(R.string.join_group) + " ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            ClickableText(
                                text = AnnotatedString(
                                    text = "Telegram",
                                    spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
                                ),
                                onClick = {
                                    uriHandler.openUri("https://t.me/YuKongA13579")
                                    hapticFeedback.performHapticFeedback(LongPress)
                                },
                                style = MaterialTheme.typography.bodyMedium + SpanStyle(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        )
    }
}