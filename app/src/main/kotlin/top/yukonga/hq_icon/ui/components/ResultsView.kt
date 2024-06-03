package top.yukonga.hq_icon.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.hq_icon.R
import top.yukonga.hq_icon.data.Response
import top.yukonga.hq_icon.utils.Download
import top.yukonga.hq_icon.utils.LoadIcon


@Composable
fun ResultsView(results: List<Response.Result>, resolution: String, corner: String) {
    Column {
        results.forEach { result ->
            ResultItemView(result, resolution, corner)
        }
    }
}


@Composable
fun ResultItemView(result: Response.Result, resolution: String, corner: String) {
    val isVisible = remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = isVisible.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier.padding(bottom = 20.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(58.dp),
                    bitmap = NetworkImage(url = result.artworkUrl512, corner = corner),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    MessageText(
                        text = result.trackName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    MessageText(
                        text = result.primaryGenreName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    MessageText(
                        text = result.artistName,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                val coroutineScope = rememberCoroutineScope()
                val context = LocalContext.current
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            coroutineScope.launch {
                                Download().downloadImage(
                                    context,
                                    result.artworkUrl512,
                                    result.trackName,
                                    resolution,
                                    corner
                                )
                            }
                        },
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.download),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    LaunchedEffect(result) {
        isVisible.value = false
        delay(300)
        isVisible.value = true
    }
}

@Composable
fun MessageText(text: String, style: TextStyle) {
    val scrollState = rememberScrollState()
    Text(
        text = text,
        style = style,
        modifier = Modifier.horizontalScroll(scrollState),
        maxLines = 1
    )
}

@Composable
fun NetworkImage(url: String, corner: String): ImageBitmap {
    val bitmapState: MutableState<Bitmap> = remember { mutableStateOf(Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(url, corner) {
        coroutineScope.launch {
            bitmapState.value = LoadIcon().loadIcon(url)
            bitmapState.value = LoadIcon().roundCorners(bitmapState.value, corner)
        }
    }
    return bitmapState.value.asImageBitmap()
}