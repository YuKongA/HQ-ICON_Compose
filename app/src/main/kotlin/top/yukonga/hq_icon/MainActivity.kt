package top.yukonga.hq_icon

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import top.yukonga.hq_icon.ui.theme.HqIconTheme
import top.yukonga.hq_icon.utils.AppContext
import top.yukonga.hq_icon.utils.Preferences
import top.yukonga.hq_icon.utils.Search
import top.yukonga.hq_icon.utils.Utils
import top.yukonga.hq_icon.viewModel.ResultsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContext.init(this)

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) window.isNavigationBarContrastEnforced = false

        val resultsViewModel: ResultsViewModel by viewModels()

        setContent {
            App(resultsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(resultsViewModel: ResultsViewModel) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val fabOffsetHeight by animateDpAsState(
        targetValue = if (scrollBehavior.state.contentOffset < -35) 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val appName = remember { mutableStateOf(Preferences().perfGet("appName") ?: "") }
    val country = remember { mutableStateOf(Preferences().perfGet("country") ?: "CN") }
    val platformCode = remember { mutableStateOf(Preferences().perfGet("platform") ?: "software") }
    val resolutionCode = remember { mutableStateOf(Preferences().perfGet("resolution") ?: "512") }
    val cornerStateCode = remember { mutableStateOf(Preferences().perfGet("corner") ?: "1") }
    val cornerState = remember { mutableStateOf(Preferences().perfGet("corner") ?: "1") }
    val limit = remember { mutableIntStateOf(15) }

    val results by resultsViewModel.results.collectAsState()
    val corner by resultsViewModel.corner.collectAsState()
    val resolution by resultsViewModel.resolution.collectAsState()

    LaunchedEffect(cornerStateCode.value, resolutionCode.value) {
        resultsViewModel.updateCorner(cornerStateCode.value)
        resultsViewModel.updateResolution(resolutionCode.value)
    }

    HqIconTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .background(MaterialTheme.colorScheme.background)
                .displayCutoutPadding(),
            topBar = {
                TopAppBar(scrollBehavior)
            },
            floatingActionButton = {
                FloatActionButton(fabOffsetHeight, appName, country, platformCode, limit, cornerState, resultsViewModel)
            },
            floatingActionButtonPosition = FabPosition.End
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    BoxWithConstraints {
                        if (maxWidth < 768.dp) {
                            Column {
                                MainCardView(appName, country)
                                SecondCardView(platformCode, cornerStateCode, resolutionCode)
                                ResultsView(results, corner, resolution)
                                Spacer(Modifier.height(padding.calculateBottomPadding()))
                            }
                        } else {
                            Column {
                                Row {
                                    Column(
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .padding(end = 20.dp)
                                    ) {
                                        MainCardView(appName, country)
                                        Spacer(modifier = Modifier.height(20.dp))
                                        SecondCardView(platformCode, cornerStateCode, resolutionCode)
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                    Column(modifier = Modifier.weight(1.0f)) {
                                        ResultsView(results, corner, resolution)
                                    }
                                }
                                Spacer(Modifier.height(padding.calculateBottomPadding()))
                            }
                        }
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
    val hapticFeedback = LocalHapticFeedback.current
    val searching = stringResource(R.string.searching)

    ExtendedFloatingActionButton(
        modifier = Modifier.offset(y = fabOffsetHeight),
        onClick = {
            Toast.makeText(AppContext.context, searching, Toast.LENGTH_SHORT).show()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
