package com.example.learningjava

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learningjava.ui.theme.LearningJavaTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// main scren of the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // this func is called when app starts
        super.onCreate(savedInstanceState)

        // get the viewmodel instance
        val myViewModel = MyViewModel(applicationContext)

        setContent {
            LearningJavaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CustomSlider(myViewModel)
                }
            }
        }

    }
}

@Composable
// display the chapter body as a markdown(diff forms of text)
fun ViewMarkdown(page: Chapter) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // row for the title on top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Card(
                modifier = Modifier.size(55.dp, 85.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                // first big letter of title
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = "${page.title.first()}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = FontFamily.Serif,
                    fontSize = 76.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.padding(5.dp, 15.dp)
            ) {
                // rest of the title
                Text(
                    text = page.title.substring(1, page.title.length),
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 32.sp,
                    lineHeight = 32.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                // read time text
                Text(
                    text = ((page.body.split(" ").size / 238F).toInt().toString() + " minute read"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.inversePrimary,
                    fontSize = 19.sp
                )
            }
        }

        // chapter body as markdown
        RichText(
            modifier = Modifier.padding(16.dp)
        ) {
            Markdown(page.body.trimIndent())
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IndexPage(
    data: List<Chapter>, pagerState: PagerState, scope: CoroutineScope, size: Float
) {
    // converts items in the data to ui rows in index page
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        itemsIndexed(data) { index, item ->
            if (index == 0) {
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                ) {
                    // "Contents" text
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        text = "Contents",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.great_vibes))
                    )
                }
            }

            if ((index) < data.size) {
                // to display chapter names
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(5.dp)
                        .fillMaxWidth()
                        .conditional(size < 100F) {
                            clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index + 1)
                                }
                            }
                        }) {


                    Row {
                        // chapter numbers card...
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.inversePrimary,
                            ), elevation = CardDefaults.cardElevation(
                                defaultElevation = 10.dp
                            ), modifier = Modifier
                                .padding(2.dp)
                                .size(56.dp, 56.dp)
                        ) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxSize(),
                                textAlign = TextAlign.Center,
                                fontSize = 35.sp,
                            )
                        }

                        Column {
                            // title of chapter
                            Text(
                                modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 2.dp),
                                text = item.title,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            // subtitle of chapter
                            Text(
                                modifier = Modifier.padding(10.dp, 2.dp, 10.dp, 5.dp),
                                text = if (item.subtitle == "default") "" else item.subtitle,
                                color = MaterialTheme.colorScheme.inversePrimary,
                                fontSize = 20.sp
                            )

                            // line on the bottom of every chapter
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.inversePrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) = Box(
    Modifier
        .background(MaterialTheme.colorScheme.primary)
        .fillMaxWidth()
        .fillMaxHeight()
) {

    // for animation of the splash
    val scale = remember {
        androidx.compose.animation.core.Animatable(0.0f)
    }

    // to run the anim in the background thread
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f, animationSpec = tween(800, easing = {
                OvershootInterpolator(4f).getInterpolation(it)
            })
        )
        delay(1000)
        navController.navigate(Screens.Home) {
            popUpTo(Screens.Splash) {
                inclusive = true
            }
        }
    }

    Image(
        painter = painterResource(id = R.drawable.splash),
        contentDescription = "",
        alignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .scale(scale.value)
    )

}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
//navigation and horizontal slider
fun CustomSlider(
    vm: MyViewModel
) {
    val chapterList = vm.getIndex() // get the chapter from viewmodel
    val pagerState = rememberPagerState() // inbuild func to remember on which page we are
    val scope = rememberCoroutineScope() // to go to a specified chapter
    var pageSize by remember { mutableFloatStateOf(0F) } // maximize and minimize
    val size: Float by animateFloatAsState(targetValue = pageSize, label = "scrollAnim")
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        // bottom nav bar
        NavigationBar(
            modifier = Modifier.height(56.dp), containerColor = MaterialTheme.colorScheme.primary
        ) {
            NavigationBarItem(selected = false, onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }, icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            })

            NavigationBarItem(selected = false, onClick = {
                pageSize = 100F
            }, icon = {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            })

            NavigationBarItem(selected = false, onClick = {
                scope.launch {
                    if (pagerState.currentPage < chapterList.size) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },

                icon = {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                })
        }
    }, content = { padding ->
        // main app screens...
        NavHost(navController = navController, startDestination = Screens.Splash) {
            composable(route = Screens.Splash) {
                SplashScreen(navController)
            }

            composable(route = Screens.Home) {
                HorizontalPager(
                    count = chapterList.size + 1,
                    state = pagerState,
                    contentPadding = PaddingValues(size.dp)
                ) { page ->
                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffset

                    val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)

                    Card(shape = RoundedCornerShape(
                        if (pageSize >= 100F) 5.dp else 0.dp
                    ), modifier = Modifier
                        .graphicsLayer {
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                        }
                        .fillMaxWidth()
                        .padding(padding)
                        .conditional(pageSize >= 100F) {
                            clickable { pageSize = 0F }
                        }

                    ) {
                        if (page == 0) {
                            IndexPage(
                                data = chapterList,
                                pagerState = pagerState,
                                scope = scope,
                                size = pageSize
                            )
                        } else {
                            Column(
                                Modifier.background(MaterialTheme.colorScheme.primary)
                            ) {
                                ViewMarkdown(page = vm.getChapter(page)!!)
                            }
                        }
                    }
                }
            }
        }
    })
}

// Extension ...
fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(this))
    } else {
        this
    }
}
