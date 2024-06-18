package top.yukonga.hq_icon

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
                    SecondCardView(platformCode, cornerStateCode, resolutionCode)
                    ResultsView(resultsState.value, cornerStateCode.value, resolutionCode.value)
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
    val hapticFeedback = LocalHapticFeedback.current

    ExtendedFloatingActionButton(
        modifier = Modifier.offset(y = fabOffsetHeight),
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
