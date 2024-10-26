package top.yukonga.hq_icon

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
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
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

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

@Composable
fun App(
    resultsViewModel: ResultsViewModel
) {
    AppTheme {
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

        val hazeStyleTopAppBar = HazeStyle(
            blurRadius = 25.dp,
            backgroundColor = if (scrollBehavior.state.heightOffset > -1) Color.Transparent else MiuixTheme.colorScheme.background,
            tint = HazeTint(
                MiuixTheme.colorScheme.background.copy(
                    if (scrollBehavior.state.heightOffset > -1) 1f
                    else lerp(1f, 0.67f, (scrollBehavior.state.heightOffset + 1) / -143f)
                )
            )
        )
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        color = Color.Transparent,
                        modifier = Modifier
                            .hazeChild(
                                state = hazeState,
                                style = hazeStyleTopAppBar
                            ),
                        title = stringResource(R.string.app_name),
                        navigationIcon = { AboutDialog() },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .haze(state = hazeState)
                        .height(getWindowSize().height.dp)
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                        .padding(top = 12.dp)
                        .padding(horizontal = 12.dp)
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    item {
                        BoxWithConstraints(
                            Modifier.padding(top = padding.calculateTopPadding())
                        ) {
                            if (maxWidth < 768.dp) {
                                Column(modifier = Modifier.navigationBarsPadding()) {
                                    MainCardView(appName, country)
                                    SecondCardView(platformCode, cornerState, resolutionCode)
                                    Button(appName, country, platformCode, limit, cornerState, resultsViewModel)
                                    ResultsView(results, corner, resolution)
                                }
                            } else {
                                Column(modifier = Modifier.navigationBarsPadding()) {
                                    Row {
                                        Column(
                                            modifier = Modifier
                                                .weight(0.8f)
                                                .padding(end = 12.dp)
                                        ) {
                                            MainCardView(appName, country)
                                            SecondCardView(platformCode, cornerState, resolutionCode)
                                            Button(appName, country, platformCode, limit, cornerState, resultsViewModel)
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
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val searching = stringResource(R.string.searching)
    val appNameEmpty = stringResource(R.string.appNameEmpty)

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        text = stringResource(R.string.submit),
        submit = true,
        onClick = {
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
        }
    )
}
