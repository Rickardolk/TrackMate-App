package com.trackmate.app.presentation.screens.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
fun DetailDeviceScreen2(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFF7F8FA)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color.White
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .myShadow(
                            color = Color(0xFFD3D1D8).copy(alpha = 0.30f) ,
                            borderRadius = 12.dp ,
                            offsetX = 6.dp ,
                            offsetY = 12.dp ,
                            blurRadius = 24.dp
                        )
                        .size(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF141718)
                    ),
                    contentPadding = PaddingValues.Zero
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_keyboard_left),
                        contentDescription = "ic keyboard left",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Detail",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF23262F),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )


            }

            Text(
                text = "Informasi Umum",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF23262F),
            )


        }
    }

}


@Preview
@Composable
private fun View() {
    DetailDeviceScreen2()
}