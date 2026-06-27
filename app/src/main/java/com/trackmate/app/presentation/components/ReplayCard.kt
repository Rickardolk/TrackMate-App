package com.trackmate.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReplayCard(
    modifier: Modifier = Modifier
) {
    val buttonNames = listOf("Hari Ini", "Kemarin", "1 Jam Lalu")
    var selectedButton by remember { mutableStateOf("Hari Ini") }

    Column(
        modifier
            .wrapContentSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Atur Waktu Pemutaran",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF323142)
        )

        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Pilih Cepat",
                fontSize = 12.sp,
                color = Color(0xFF323142)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                buttonNames.forEach { name ->
                    val isSelected = selectedButton == name
                    
                    Button(
                        onClick = { selectedButton = name },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF141718) else Color.Transparent,
                            contentColor = if (isSelected) Color.White else Color(0xFF141718)
                        ),
                        border = if (!isSelected) BorderStroke(1.dp, Color(0xFF141718)) else null,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun View() {
    ReplayCard()
}
