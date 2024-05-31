package top.yukonga.hq_icon

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.hq_icon.data.Data
import top.yukonga.hq_icon.data.Response
import top.yukonga.hq_icon.ui.theme.HqIconTheme
import top.yukonga.hq_icon.utils.AppContext
import top.yukonga.hq_icon.utils.Download
import top.yukonga.hq_icon.utils.LoadIcon
import top.yukonga.hq_icon.utils.Preferences
import top.yukonga.hq_icon.utils.Search
import top.yukonga.hq_icon.utils.Utils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContext.init(this)

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) window.isNavigationBarContrastEnforced = false

        setContent {
            App()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun App() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val fabOffsetHeight by animateDpAsState(
        targetValue = if (scrollBehavior.state.contentOffset < 0) 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    Log.d("heightOffset", scrollBehavior.state.heightOffset.toString())
    Log.d("contentOffset", scrollBehavior.state.contentOffset.toString())
    Log.d("heightOffsetLimit", scrollBehavior.state.heightOffsetLimit.toString())

    val appName = remember { mutableStateOf(Preferences().perfGet("appName") ?: "") }
    val country = remember { mutableStateOf(Preferences().perfGet("country") ?: "CN") }
    val platformCode = remember { mutableStateOf(Preferences().perfGet("platform") ?: "software") }
    val resolutionCode = remember { mutableStateOf(Preferences().perfGet("resolution") ?: "512") }
    val cornerStateCode = remember { mutableStateOf(Preferences().perfGet("corner") ?: "1") }
    val cornerState = remember { mutableStateOf(Preferences().perfGet("corner") ?: "1") }
    val limit = remember { mutableIntStateOf(15) }
    val resultsState: MutableState<List<Response.Result>> = remember { mutableStateOf(emptyList()) }

    HqIconTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TopAppBar(scrollBehavior) },
            floatingActionButton = { FloatActionButton(fabOffsetHeight, appName, country, platformCode, limit, resultsState, cornerStateCode, cornerState) },
            floatingActionButtonPosition = FabPosition.End
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    MainCardView(appName, country)
                    SecondView(Data().platformNames, Data().resolutionNames, Data().cornerStateNames, platformCode, resolutionCode, cornerStateCode)
                    ResultsView(resultsState.value, resolutionCode.value, cornerStateCode.value)
                    Spacer(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        actions = { AboutDialog() },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog() {
    var showDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    IconButton(
        onClick = { showDialog = true }) {
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
                                onClick = { uriHandler.openUri("https://github.com/YuKongA/HQ-ICON_Compose") },
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
                                onClick = { uriHandler.openUri("https://t.me/YuKongA13579") },
                                style = MaterialTheme.typography.bodyMedium + SpanStyle(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun FloatActionButton(
    fabOffsetHeight: Dp,
    term: MutableState<String>,
    country: MutableState<String>,
    platform: MutableState<String>,
    limit: MutableState<Int>,
    resultsState: MutableState<List<Response.Result>>,
    cornerStateCode: MutableState<String>,
    cornerState: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()
    ExtendedFloatingActionButton(
        modifier = Modifier.offset(y = fabOffsetHeight),
        onClick = {
            coroutineScope.launch {
                if (term.value != "") {
                    val results = Search().search(term.value, country.value, platform.value, limit.value)
                    val response = Utils().json.decodeFromString<Response.Root>(results)
                    cornerState.value = cornerStateCode.value
                    resultsState.value = response.results
                    Preferences().perfSet("appName", term.value)
                    Preferences().perfSet("country", country.value)
                    Preferences().perfSet("platform", platform.value)
                    Preferences().perfSet("corner", cornerState.value)
                }
            }
        },
        containerColor = Color(0xFF0D84FF),
        contentColor = Color(0xFFFFFFFF),
    ) {
        Icon(
            modifier = Modifier.height(20.dp),
            imageVector = Icons.Filled.Check,
            contentDescription = null

        )
        Spacer(
            modifier = Modifier.width(8.dp)
        )
        Text(
            text = stringResource(R.string.submit),
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
fun MainCardView(
    appName: MutableState<String>,
    country: MutableState<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppNameView(appName)
        CountryView(country)
    }
}

@Composable
fun AppNameView(
    appName: MutableState<String>
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = appName.value,
        onValueChange = { appName.value = it },
        label = { Text(stringResource(R.string.appName)) },
        shape = RoundedCornerShape(10.dp),
        maxLines = 1,
        leadingIcon = { Icon(imageVector = Icons.Outlined.ImageSearch, null) },
    )
}

@Composable
fun CountryView(
    country: MutableState<String>
) {
    TextFieldWithDropdown(
        text = country,
        items = Data().country,
        label = stringResource(R.string.country),
        leadingIcon = Icons.Outlined.TravelExplore,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithDropdown(
    text: MutableState<String>,
    items: List<String>,
    label: String,
    leadingIcon: ImageVector
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = it },
    ) {
        OutlinedTextField(
            value = text.value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(10.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isDropdownExpanded) },
            leadingIcon = { Icon(imageVector = leadingIcon, null) },
        )
        DropdownMenu(
            modifier = Modifier
                .exposedDropdownSize()
                .heightIn(max = 250.dp),
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        text.value = item
                        isDropdownExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SecondView(
    platform: List<String>,
    resolution: List<String>,
    cornerState: List<String>,
    platformCode: MutableState<String>,
    resolutionCode: MutableState<String>,
    cornerStateCode: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            PlatformView(platform, platformCode)
        }
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            CornerView(cornerState, cornerStateCode)
        }
    }
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        ResolutionView(resolution, resolutionCode)
    }
}

@Composable
fun PlatformView(
    platform: List<String>,
    platformCode: MutableState<String>
) {
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
    cornerState: List<String>,
    cornerStateCode: MutableState<String>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.corner)
        )
        val cornerStateName = Preferences().perfGet("corner")?.let { Data().cornerStateName(it) } ?: cornerState[0]
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(cornerStateName) }
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            cornerState.forEach { text ->
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                                cornerStateCode.value = Data().cornerStateCode(text)
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
    resolution: List<String>,
    resolutionCode: MutableState<String>
) {
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
                    modifier = Modifier.size(50.dp),
                    bitmap = networkImage(url = result.artworkUrl512, corner = corner),
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
fun networkImage(url: String, corner: String): ImageBitmap {
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