package top.yukonga.hq_icon

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.hq_icon.data.Response
import top.yukonga.hq_icon.ui.components.AboutDialog
import top.yukonga.hq_icon.ui.components.MainCardView
import top.yukonga.hq_icon.ui.components.ResultsView
import top.yukonga.hq_icon.ui.components.SecondCardView
import top.yukonga.hq_icon.ui.theme.AppTheme
import top.yukonga.hq_icon.utils.AppContext
import top.yukonga.hq_icon.utils.Preferences
import top.yukonga.hq_icon.utils.Search
import top.yukonga.hq_icon.utils.Utils
import top.yukonga.hq_icon.viewModel.ResultsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContext.init(this)

        val resultsViewModel: ResultsViewModel by viewModels()

        setContent {
            val colorMode = remember { mutableIntStateOf(Preferences().perfGet("colorMode")?.toInt() ?: 0) }
            val darkMode = colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)

            DisposableEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { darkMode },
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }

                onDispose {}
            }
            App(resultsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    resultsViewModel: ResultsViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val listState = rememberLazyListState()
    var fabVisible by remember { mutableStateOf(true) }
    var scrollDistance by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isScrolledToEnd =
                    (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 && (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.size
                        ?: 0) < listState.layoutInfo.viewportEndOffset)

                val delta = available.y
                if (!isScrolledToEnd) {
                    scrollDistance += delta
                    if (scrollDistance < -50f) {
                        if (fabVisible) fabVisible = false
                        scrollDistance = 0f
                    } else if (scrollDistance > 50f) {
                        if (!fabVisible) fabVisible = true
                        scrollDistance = 0f
                    }
                }
                return Offset.Zero
            }
        }
    }

    val fabOffsetHeight by animateDpAsState(
        targetValue = if (fabVisible) 0.dp else 74.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(), // 74.dp = FAB + FAB Padding
        animationSpec = tween(durationMillis = 350), label = ""
    )

    val appName = remember { mutableStateOf(Preferences().perfGet("appName") ?: "") }
    val country = remember { mutableStateOf(Preferences().perfGet("country") ?: "CN") }
    val platformCode = remember { mutableStateOf(Preferences().perfGet("platform") ?: "software") }
    val resolutionCode = remember { mutableStateOf(Preferences().perfGet("resolution") ?: "512") }
    val cornerState = remember { mutableStateOf(Preferences().perfGet("corner") ?: "1") }
    val limit = remember { mutableIntStateOf(15) }

    val results by resultsViewModel.results.collectAsState()
    val corner by resultsViewModel.corner.collectAsState()
    val resolution by resultsViewModel.resolution.collectAsState()

    LaunchedEffect(cornerState.value, resolutionCode.value) {
        resultsViewModel.updateCorner(cornerState.value)
        resultsViewModel.updateResolution(resolutionCode.value)
    }

    AppTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .displayCutoutPadding()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(scrollBehavior)
                }
            ) { padding ->
                Box(
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(top = padding.calculateTopPadding())
                            .padding(horizontal = 20.dp)
                    ) {
                        item {
                            BoxWithConstraints {
                                if (maxWidth < 768.dp) {
                                    Column(modifier = Modifier.navigationBarsPadding()) {
                                        MainCardView(appName, country)
                                        SecondCardView(platformCode, cornerState, resolutionCode)
                                        ResultsView(results, corner, resolution)
                                    }
                                } else {
                                    Column(modifier = Modifier.navigationBarsPadding()) {
                                        Row {
                                            Column(
                                                modifier = Modifier
                                                    .weight(0.8f)
                                                    .padding(end = 20.dp)
                                            ) {
                                                MainCardView(appName, country)
                                                SecondCardView(platformCode, cornerState, resolutionCode)
                                            }
                                            Column(modifier = Modifier.weight(1.0f)) {
                                                ResultsView(results, corner, resolution)
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .padding(18.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatActionButton(fabOffsetHeight, appName, country, platformCode, limit, cornerState, resultsViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1
            )
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
        navigationIcon = { AboutDialog() },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun FloatActionButton(
    fabOffsetHeight: Dp,
    term: MutableState<String>,
    country: MutableState<String>,
    platform: MutableState<String>,
    limit: MutableState<Int>,
    cornerState: MutableState<String>,
    resultsViewModel: ResultsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val searching = stringResource(R.string.searching)
    val appNameEmpty = stringResource(R.string.appNameEmpty)

    ExtendedFloatingActionButton(
        modifier = Modifier.offset(y = fabOffsetHeight),
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            if (term.value == "") {
                Toast.makeText(context, appNameEmpty, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, searching, Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    if (term.value != "") {
                        val results = Search().search(term.value, country.value, platform.value, limit.value)
                        val response = Utils().json.decodeFromString<Response.Root>(results)
                        resultsViewModel.updateResults(response.results)

                        Preferences().perfSet("appName", term.value)
                        Preferences().perfSet("country", country.value)
                        Preferences().perfSet("platform", platform.value)
                        Preferences().perfSet("corner", cornerState.value)
                    }
                }
            }
        },
        containerColor = Color(0xFF0D84FF),
        contentColor = Color(0xFFFFFFFF),
    ) {
        Icon(
            modifier = Modifier.height(20.dp),
            imageVector = Icons.Filled.Check,
            contentDescription = stringResource(R.string.submit)
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
