package top.yukonga.hq_icon

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
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
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContext.init(this)

        val resultsViewModel: ResultsViewModel by viewModels()

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            App(resultsViewModel)
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun App(
    resultsViewModel: ResultsViewModel
) {
    AppTheme {
        val focusManager = LocalFocusManager.current
        val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

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

        val hazeState = remember { HazeState() }
        val hazeStyle = HazeStyle(
            backgroundColor = MiuixTheme.colorScheme.background,
            tint = HazeTint(
                MiuixTheme.colorScheme.background.copy(
                    if (scrollBehavior.state.collapsedFraction <= 0f) 1f
                    else lerp(1f, 0.67f, (scrollBehavior.state.collapsedFraction))
                )
            )
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null,
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        color = Color.Transparent,
                        modifier = Modifier
                            .hazeEffect(hazeState) {
                                style = hazeStyle
                                blurRadius = 25.dp
                                noiseFactor = 0f
                            },
                        title = stringResource(R.string.app_name),
                        navigationIcon = { AboutDialog() },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { padding ->
                BoxWithConstraints(
                    Modifier.hazeSource(state = hazeState)
                ) {
                    if (maxWidth < 768.dp) {
                        LazyColumn(
                            modifier = Modifier
                                .height(getWindowSize().height.dp)
                                .overScrollVertical()
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                                .padding(top = 12.dp)
                                .padding(horizontal = 12.dp),
                            overscrollEffect = null
                        ) {
                            item {
                                Column(modifier = Modifier.navigationBarsPadding()) {
                                    Spacer(Modifier.height(padding.calculateTopPadding()))
                                    MainCardView(appName, country)
                                    SecondCardView(platformCode, cornerState, resolutionCode)
                                    Button(appName, country, platformCode, limit, cornerState, resultsViewModel)
                                    ResultsView(results, corner, resolution)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                        ) {

                            LazyColumn(
                                modifier = Modifier
                                    .height(getWindowSize().height.dp)
                                    .overScrollVertical()
                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                                    .padding(top = 12.dp)
                                    .padding(horizontal = 12.dp)
                                    .weight(1f),
                                overscrollEffect = null
                            ) {
                                item {
                                    Spacer(Modifier.height(padding.calculateTopPadding()))
                                    Column(
                                        modifier = Modifier.navigationBarsPadding()
                                    ) {
                                        Row {
                                            Column(
                                                modifier = Modifier.weight(0.8f)
                                            ) {
                                                MainCardView(appName, country)
                                                SecondCardView(platformCode, cornerState, resolutionCode)
                                                Button(appName, country, platformCode, limit, cornerState, resultsViewModel)
                                            }
                                        }
                                    }

                                }
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(getWindowSize().height.dp)
                                    .overScrollVertical()
                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                                    .padding(top = 12.dp, end = 12.dp),
                                overscrollEffect = null
                            ) {
                                item {
                                    Spacer(Modifier.height(padding.calculateTopPadding()))
                                    Column(modifier = Modifier.navigationBarsPadding()) {
                                        ResultsView(results, corner, resolution)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Button(
    term: MutableState<String>,
    country: MutableState<String>,
    platform: MutableState<String>,
    limit: MutableState<Int>,
    cornerState: MutableState<String>,
    resultsViewModel: ResultsViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val searching = stringResource(R.string.searching)
    val appNameEmpty = stringResource(R.string.appNameEmpty)

    TextButton(
        text = stringResource(R.string.submit),
        colors = ButtonDefaults.textButtonColorsPrimary(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        onClick = {
            focusManager.clearFocus()
            if (term.value == "") {
                Toast.makeText(context, appNameEmpty, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, searching, Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    if (term.value != "") {
                        val results = Search().search(term.value, country.value, platform.value, limit.value)
                        results.takeIf { it.isBlank() }?.also {
                            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        val response = Utils().json.decodeFromString<Response.Root>(results)
                        resultsViewModel.updateResults(response.results)
                        Preferences().perfSet("appName", term.value)
                        Preferences().perfSet("country", country.value)
                        Preferences().perfSet("platform", platform.value)
                        Preferences().perfSet("corner", cornerState.value)
                    }
                }
            }
        }
    )
}
