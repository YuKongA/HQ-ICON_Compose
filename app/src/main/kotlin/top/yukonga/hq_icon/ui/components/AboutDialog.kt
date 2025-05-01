package top.yukonga.hq_icon.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.hq_icon.BuildConfig
import top.yukonga.hq_icon.R
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun AboutDialog() {
    val showDialog = remember { mutableStateOf(false) }
    val icon = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground))
    val focusManager = LocalFocusManager.current

    IconButton(
        modifier = Modifier.padding(start = 16.dp),
        onClick = {
            showDialog.value = true
            focusManager.clearFocus()
        },
        holdDownState = showDialog.value
    ) {
        Image(
            painter = icon,
            contentDescription = "About",
            modifier = Modifier
                .size(42.dp)
                .graphicsLayer(
                    scaleX = 0.5f,
                    scaleY = 0.5f
                ),
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface),
            contentScale = ContentScale.None
        )
    }

    SuperDialog(
        show = showDialog,
        title = stringResource(R.string.about),
        onDismissRequest = {
            dismissDialog(showDialog)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(color = MiuixTheme.colorScheme.primary, shape = SmoothRoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = icon,
                    contentDescription = "About",
                    modifier = Modifier.graphicsLayer(
                        scaleX = 0.58f,
                        scaleY = 0.58f
                    ),
                    contentScale = ContentScale.None
                )
            }
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.app_name),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                )
            }
        }
        val uriHandler = LocalUriHandler.current
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.view_source) + " ",
            )
            Text(
                text = AnnotatedString(
                    text = "GitHub",
                    spanStyle = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = MiuixTheme.colorScheme.primary
                    )
                ),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    onClick = {
                        uriHandler.openUri("https://github.com/YuKongA/HQ-ICON_Compose")
                    }
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.join_channel) + " ",
            )
            Text(
                text = AnnotatedString(
                    text = "Telegram",
                    spanStyle = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = MiuixTheme.colorScheme.primary
                    )
                ),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    onClick = {
                        uriHandler.openUri("https://t.me/YuKongA13579")
                    },
                )
            )
        }
    }
}