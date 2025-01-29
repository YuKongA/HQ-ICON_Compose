package top.yukonga.hq_icon.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.hq_icon.R
import top.yukonga.hq_icon.data.Response
import top.yukonga.hq_icon.utils.Download
import top.yukonga.hq_icon.utils.LoadIcon
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ResultsView(results: List<Response.Result>, corner: String, resolution: String) {
    Column {
        results.forEach { result ->
            ResultItemView(result, corner, resolution)
        }
    }
}

@Composable
fun ResultItemView(result: Response.Result, corner: String, resolution: String) {
    val isVisible = remember { mutableStateOf(false) }
    val isDialogOpen = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = isVisible.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(58.dp)
                        .clickable { isDialogOpen.value = true },
                    bitmap = networkImage(
                        url = result.artworkUrl512,
                        corner = corner,
                        resolution = resolution.toInt()
                    ),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    MessageText(
                        text = result.trackName,
                        style = MiuixTheme.textStyles.body1
                    )
                    MessageText(
                        text = result.primaryGenreName,
                        style = MiuixTheme.textStyles.body2
                    )
                    MessageText(
                        text = result.artistName,
                        style = MiuixTheme.textStyles.subtitle
                    )
                }
                IconButton(
                    modifier = Modifier
                        .padding(start = 12.dp),
                    minHeight = 30.dp,
                    cornerRadius = 20.dp,
                    backgroundColor = MiuixTheme.colorScheme.primary,
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            Download().downloadImage(
                                context,
                                result.artworkUrl512,
                                result.trackName,
                                resolution,
                                corner
                            )
                        }
                    }
                ) {
                    Surface(
                        color = Color.Transparent,
                        modifier = Modifier
                            .padding(horizontal = 12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.download),
                            color = MiuixTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    if (isDialogOpen.value) {
        Dialog(
            onDismissRequest = { isDialogOpen.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            content = {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = networkImage(
                            url = result.artworkUrl512,
                            corner = corner,
                            resolution = resolution.toInt()
                        ),
                        contentDescription = null
                    )
                }
            }
        )
    }

    LaunchedEffect(result) {
        isVisible.value = false
        delay(300)
        isVisible.value = true
    }
}

@Composable
fun MessageText(text: String, style: TextStyle) {
    Text(
        text = text,
        style = style,
        maxLines = 1,
        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE, initialDelayMillis = 3000, velocity = 15.dp)
    )
}

@Composable
fun networkImage(url: String, corner: String, resolution: Int): ImageBitmap {
    val bitmapState: MutableState<Bitmap> = remember { mutableStateOf(createBitmap(resolution, resolution)) }
    val realUrl = url.replace("512x512bb.jpg", "${resolution}x${resolution}bb.png")
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(url, corner) {
        coroutineScope.launch {
            bitmapState.value = LoadIcon().loadIcon(realUrl)
            bitmapState.value = LoadIcon().roundCorners(bitmapState.value, corner)
        }
    }
    return bitmapState.value.asImageBitmap()
}