package com.trackmate.app.presentation.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.R
import com.trackmate.app.presentation.navigation.Screen
import com.trackmate.app.utils.myShadow
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onNavigateToAuth: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            TextButton(
                onClick = onNavigateToAuth,
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFD7D7D7)
                ),
                modifier = Modifier
                    .padding(end = 36.dp)
                    .align(Alignment.End)
            ) {
                Text(
                    text = "Lewati",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { position ->
                Column(
                    modifier
                        .height(392.dp)
                        .padding(horizontal = 36.dp)
                        .padding(bottom = 32.dp)
                        .fillMaxWidth()
                        .myShadow(
                            color = Color(0xFF000000).copy(alpha = 0.35f),
                            offsetY = 8.dp ,
                            borderRadius = 40.dp ,
                            blurRadius = 20.dp ,
                            spread = 10.dp
                        )
                        .clip(shape = RoundedCornerShape(40.dp))
                ) {
                    Image(
                        painter = painterResource(onboardingPages[position].imageRes),
                        contentDescription = "image onboarding $position",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(onboardingPages.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        val color = if (isSelected) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.outlineVariant
                        val width by animateDpAsState(if (isSelected) 28.dp else 10.dp, label = "dotWidth")
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }

            Text(
                text = onboardingPages[pagerState.currentPage].title,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF23262F),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = onboardingPages[pagerState.currentPage].description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8E9295),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .padding(horizontal = 36.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier
                    .width(154.dp)
                    .height(64.dp)
                    .myShadow(
                        color = Color(0xFF0F0F0F).copy(alpha = 0.12f) ,
                        borderRadius = 16.dp ,
                        offsetY = 40.dp ,
                        blurRadius = 32.dp ,
                        spread = (- 8).dp
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFCFCFD))
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically

            ) {
                val isLeftEnabled = pagerState.currentPage > 0
                IconButton(
                    onClick = {
                        if (isLeftEnabled) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    enabled = isLeftEnabled
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "ic arrow left",
                        modifier = Modifier
                            .height(25.dp)
                            .width(24.dp),
                        tint = if (isLeftEnabled) Color(0xFF23262F) else Color(0xFFB1B5C3)
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(24.dp)
                        .background(
                            color = Color(0xFFE6E8EC) ,
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                IconButton(
                    onClick = {
                        if (pagerState.currentPage == onboardingPages.size - 1) {
                            onNavigateToAuth()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_right),
                        contentDescription = "ic arrow right",
                        modifier = Modifier
                            .height(25.dp)
                            .width(24.dp),
                        tint = Color(0xFF23262F)
                    )
                }


            }









        }
    }

}


@Preview
@Composable
private fun View() {
    OnboardingScreen(
        onNavigateToAuth = {}
    )
    
}