package com.trackmate.app.presentation.screens.auth

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.R
import com.trackmate.app.utils.myShadow

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp)
    ){
        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo_trackmate),
                contentDescription = "ic logo",
                tint = Color(0xFF141718)
            )

            Text(
                text = "Selamat Datang\ndi TrackMate",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp,
                color = Color(0xFF323142),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(top = 50.dp))
            Button(
                onClick = onNavigateToLogin,
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF141718),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .myShadow(
                        color = Color(0xFF000000).copy(0.25f),
                        offsetY = 8.dp,
                        offsetX = 4.dp,
                        blurRadius = 24.dp
                    )
            ) {
                Text(
                    text = "Masuk",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNavigateToRegister,
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3E3E3),
                    contentColor = Color(0xFFB1B1B1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = "Daftar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = {},
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .myShadow(
                            offsetY = 2.dp,
                            blurRadius = 4.dp,
                            borderRadius = 10.dp,
                            color = Color(0xFF000000).copy(alpha = 0.05f)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Demo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF23262F)

                        )

                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "ic arrow right",
                            tint = Color(0xFF23262F),
                            modifier = Modifier.size(24.dp)
                        )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Masuk dengan Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFACADB9)
            )

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {},
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD44638).copy(0.25f),
                    contentColor = Color(0xFFD44638)
                ),
                modifier = Modifier
                    .wrapContentSize(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "GOOGLE",
                    letterSpacing = 2.8.sp,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD44638),
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 28.dp)
                )
            }




        }
    }




}

@Preview
@Composable
private fun View() {
    WelcomeScreen(
        onNavigateToLogin = {},
        onNavigateToRegister = {}
    )
    
}