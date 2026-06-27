package com.trackmate.app.presentation.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.presentation.theme.TrackMateTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onNavigateToAuth: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size})
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(top = 40.dp, bottom = 60.dp)
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    if (pagerState.currentPage > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Text(
                    text = "Kembali",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (pagerState.currentPage > 0) MaterialTheme.colorScheme.secondary
                            else Color.Transparent
                )
            }

            TextButton(
                onClick = onNavigateToAuth
            ) {
                Text(
                    text = "Lewati",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier.height(40.dp))

        HorizontalPager(
            state = pagerState,
            modifier.fillMaxWidth()
        ) { position ->
            PagerScreen(page = onboardingPages[position])
        }

        Spacer(modifier.weight(1f))

        Row(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                    val width = if (pagerState.currentPage == iteration) 24.dp else 10.dp
                    Box(
                        modifier
                            .height(10.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == onboardingPages.size - 1) {
                        onNavigateToAuth()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp) ,
                modifier = Modifier
                    .height(60.dp)
                    .wrapContentWidth()
            ) {

                Row(
                    modifier
                        .padding(start = 32.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == onboardingPages.size -1) "Mulai"
                            else "Lanjut",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            )
                    )
                }
            }
        }
    }

}


@Composable
fun PagerScreen(
    modifier: Modifier = Modifier,
    page: OnboardingPage
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFFE0E0E0))
        )

        Spacer(modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}


@Preview (name = "Onboarding Screen Full", showBackground = true)
@Composable
private fun View () {
    TrackMateTheme {
        OnboardingScreen(
            onNavigateToAuth = {
                println("Navigasi ke Auth ditekan")
            }
        )
    }
}

@Preview(
name = "Pager Component Item",
showBackground = true,
backgroundColor = 0xFFFFFFFF // Latar putih
)
@Composable
fun PagerScreenPreview() {
    PagerScreen(
        // Data dummy murni untuk melihat desain satu halaman
        page = OnboardingPage(
            title = "Real-time Tracking",
            description = "Pantau setiap pergerakan kendaraan anda secara langsung",
            imageRes = 0
        )
    )
}

